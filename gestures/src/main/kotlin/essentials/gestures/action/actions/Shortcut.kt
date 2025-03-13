/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import android.graphics.*
import android.util.*
import androidx.compose.foundation.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.core.graphics.drawable.*
import essentials.gestures.action.*
import essentials.gestures.action.ui.*
import essentials.gestures.shortcut.*
import essentials.ui.navigation.*
import injekt.*
import java.io.*

@Provide class ShortcutActionFactory(private val sendActionIntent: sendActionIntent) : ActionFactory {
  override suspend fun createAction(id: String): Action<*>? {
    if (!id.startsWith(BASE_ID)) return null

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
      permissions = listOf(ActionSystemOverlayPermission::class),
      icon = { Image(bitmap = icon, contentDescription = null) }
    )
  }

  @Suppress("DEPRECATION")
  override suspend fun execute(id: String): ActionExecutorResult<*>? {
    if (!id.startsWith(BASE_ID)) return null

    val tmp = id.split(ACTION_DELIMITER)
    val intent = Intent.getIntent(tmp[2])
    return sendActionIntent(intent, null)
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
