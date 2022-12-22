/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ACTION_DELIMITER
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.essentials.gestures.action.FloatingWindowActionsEnabled
import com.ivianuu.essentials.gestures.action.ui.FloatingWindowsPickerKey
import com.ivianuu.essentials.gestures.action.ui.LocalActionImageSizeModifier
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerKey
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import java.io.ByteArrayOutputStream

@Provide class ShortcutActionFactory(
  private val actionIntentSender: ActionIntentSender
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val tmp = id.split(ACTION_DELIMITER)
    val label = tmp[1]

    val iconBytes = Base64.decode(tmp[3], 0)
    val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).toImageBitmap()
    return Action<ActionId>(
      id = id,
      title = label,
      unlockScreen = true,
      closeSystemDialogs = true,
      enabled = true,
      permissions = listOf(typeKeyOf<ActionSystemOverlayPermission>()),
      icon = {
        Image(
          modifier = LocalActionImageSizeModifier.current,
          bitmap = icon
        )
      }
    )
  }

  @Suppress("DEPRECATION")
  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val tmp = id.split(ACTION_DELIMITER)
    val intent = Intent.getIntent(tmp[2])
    val isFloating = tmp[4].toBoolean()
    return ActionExecutor<ActionId> { actionIntentSender(intent, isFloating, null) }
  }
}

context(ResourceProvider) @Provide class ShortcutActionPickerDelegate(
  private val floatingWindowActionsEnabled: FloatingWindowActionsEnabled,
  private val navigator: Navigator
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = loadResource(R.string.es_action_shortcut)
  override val icon: @Composable () -> Unit
    get() = { Icon(R.drawable.es_ic_content_cut) }

  override suspend fun pickAction(): ActionPickerKey.Result? {
    val shortcut = navigator.push(ShortcutPickerKey) ?: return null
    val name = shortcut.name
    val icon = shortcut.icon.toBitmap()
    val stream = ByteArrayOutputStream()
    icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val iconBytes = stream.toByteArray()

    val isFloating = floatingWindowActionsEnabled.value &&
        navigator.push(FloatingWindowsPickerKey(name)) ?: return null

    val key = "$BASE_ID$ACTION_DELIMITER" +
        "$name$ACTION_DELIMITER" +
        "${shortcut.intent.toUri(0)}$ACTION_DELIMITER" +
        "${Base64.encodeToString(iconBytes, 0)}$ACTION_DELIMITER" +
        "$isFloating"

    return ActionPickerKey.Result.Action(key)
  }
}

private const val BASE_ID = "shortcut"
