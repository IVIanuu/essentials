/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcutpicker

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toDrawable
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IOContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface ShortcutRepository {
  val shortcuts: Flow<List<Shortcut>>

  suspend fun extractShortcut(shortcutRequestResult: Intent): Shortcut
}

context(AppContext, BroadcastsFactory) @Provide class ShortcutRepositoryImpl(
  private val coroutineContext: IOContext
) : ShortcutRepository {
  override val shortcuts: Flow<List<Shortcut>>
    get() = broadcasts(
      Intent.ACTION_PACKAGE_ADDED,
      Intent.ACTION_PACKAGE_REMOVED,
      Intent.ACTION_PACKAGE_CHANGED,
      Intent.ACTION_PACKAGE_REPLACED
    ).onStart<Any?> { emit(Unit) }
      .mapLatest {
        withContext(coroutineContext) {
          val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
          packageManager.queryIntentActivities(shortcutsIntent, 0)
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
                  name = resolveInfo.loadLabel(packageManager).toString(),
                  icon = resolveInfo.loadIcon(packageManager)
                )
              }.getOrNull()
            }
            .filterNotNull()
            .sortedBy { it.name }
        }
      }
      .distinctUntilChanged()

  override suspend fun extractShortcut(shortcutRequestResult: Intent) = withContext(coroutineContext) {
    val intent =
      shortcutRequestResult.getParcelableExtra<Intent>(Intent.EXTRA_SHORTCUT_INTENT)!!
    val name = shortcutRequestResult.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)!!
    val bitmapIcon =
      shortcutRequestResult.getParcelableExtra<Bitmap>(Intent.EXTRA_SHORTCUT_ICON)
    val iconResource =
      shortcutRequestResult.getParcelableExtra<Intent.ShortcutIconResource>(Intent.EXTRA_SHORTCUT_ICON_RESOURCE)

    @Suppress("DEPRECATION") val icon = when {
      bitmapIcon != null -> bitmapIcon.toDrawable(resources)
      iconResource != null -> {
        val resources =
          packageManager.getResourcesForApplication(iconResource.packageName)
        val id =
          resources.getIdentifier(iconResource.resourceName, null, null)
        resources.getDrawable(id)
      }
      else -> error("No icon provided $shortcutRequestResult")
    }

    Shortcut(intent, name, icon)
  }
}
