/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import android.graphics.*
import android.util.*
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.shortcut.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import java.io.*

@Provide class ShortcutActionFactory(private val intentSender: ActionIntentSender) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val tmp = id.split(ACTION_DELIMITER)
    val label = tmp[1]

    val iconBytes = Base64.decode(tmp[3], 0)
    val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).asImageBitmap()
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
          bitmap = icon,
          contentDescription = null
        )
      }
    )
  }

  @Suppress("DEPRECATION")
  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val tmp = id.split(ACTION_DELIMITER)
    val intent = Intent.getIntent(tmp[2])
    return ActionExecutor<ActionId> { intentSender(intent, null) }
  }
}

@Provide class ShortcutActionPickerDelegate : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = "Shortcut"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.ContentCut, null) }

  override suspend fun pickAction(navigator: Navigator): ActionPickerScreen.Result? {
    val shortcut = navigator.push(ShortcutPickerScreen()) ?: return null
    val name = shortcut.name
    val icon = shortcut.icon.toBitmap()
    val stream = ByteArrayOutputStream()
    icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val iconBytes = stream.toByteArray()

    val key = "$BASE_ID$ACTION_DELIMITER" +
        "$name$ACTION_DELIMITER" +
        "${shortcut.intent.toUri(0)}$ACTION_DELIMITER" +
        "${Base64.encodeToString(iconBytes, 0)}$ACTION_DELIMITER"

    return ActionPickerScreen.Result.Action(key)
  }
}

private const val BASE_ID = "shortcut"
