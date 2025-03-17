/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.shortcut

import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.graphics.drawable.*
import androidx.compose.runtime.*
import androidx.core.graphics.drawable.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class Shortcut(val intent: Intent, val name: String, val icon: Drawable)

@Tag typealias getShortcutsResult = List<Shortcut>
typealias getShortcuts = suspend () -> getShortcutsResult

@Provide suspend fun getShortcuts(
  coroutineContexts: CoroutineContexts,
  context: Application
): getShortcutsResult = withContext(coroutineContexts.io) {
  val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
  context.packageManager.queryIntentActivities(shortcutsIntent, 0)
    .parMap { resolveInfo ->
      catch {
        Shortcut(
          intent = Intent().apply {
            action = Intent.ACTION_CREATE_SHORTCUT
            component = ComponentName(
              resolveInfo.activityInfo.packageName,
              resolveInfo.activityInfo.name
            )
          },
          name = resolveInfo.loadLabel(context.packageManager).toString(),
          icon = resolveInfo.loadIcon(context.packageManager)
        )
      }.getOrNull()
    }
    .filterNotNull()
    .sortedBy { it.name }
}

@Tag typealias extractShortcutResult = Shortcut
typealias extractShortcut = suspend (Intent) -> extractShortcutResult

@Provide suspend fun extractShortcut(
  shortcutRequestResult: Intent,
  context: Application,
  coroutineContexts: CoroutineContexts
): extractShortcutResult = withContext(coroutineContexts.io) {
  val intent =
    shortcutRequestResult.getParcelableExtra<Intent>(Intent.EXTRA_SHORTCUT_INTENT)!!
  val name = shortcutRequestResult.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)!!
  val bitmapIcon =
    shortcutRequestResult.getParcelableExtra<Bitmap>(Intent.EXTRA_SHORTCUT_ICON)
  val iconResource =
    shortcutRequestResult.getParcelableExtra<Intent.ShortcutIconResource>(Intent.EXTRA_SHORTCUT_ICON_RESOURCE)

  @Suppress("DEPRECATION") val icon = when {
    bitmapIcon != null -> bitmapIcon.toDrawable(context.resources)
    iconResource != null -> {
      val resources =
        context.packageManager.getResourcesForApplication(iconResource.packageName)
      val id =
        resources.getIdentifier(iconResource.resourceName, null, null)
      resources.getDrawable(id)
    }
    else -> error("No icon provided $shortcutRequestResult")
  }

  Shortcut(intent, name, icon)
}
