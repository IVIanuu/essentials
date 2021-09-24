/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.shortcutpicker

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toDrawable
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.getOrNull
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

typealias Shortcuts = List<Shortcut>

@Provide fun shortcuts(
  broadcastsFactory: BroadcastsFactory,
  dispatcher: IODispatcher,
  packageManager: PackageManager
): Flow<Shortcuts> = merge(
  broadcastsFactory(Intent.ACTION_PACKAGE_ADDED),
  broadcastsFactory(Intent.ACTION_PACKAGE_REMOVED),
  broadcastsFactory(Intent.ACTION_PACKAGE_CHANGED),
  broadcastsFactory(Intent.ACTION_PACKAGE_REPLACED)
)
  .map { Unit }
  .onStart { emit(Unit) }
  .mapLatest {
    withContext(dispatcher) {
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

typealias ExtractShortcutUseCase = (Intent) -> Shortcut

@Provide fun extractShortcutUseCase(
  context: AppContext,
  packageManager: PackageManager
): ExtractShortcutUseCase = { shortcutRequestResult ->
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
        packageManager.getResourcesForApplication(iconResource.packageName)
      val id =
        resources.getIdentifier(iconResource.resourceName, null, null)
      resources.getDrawable(id)
    }
    else -> error("No icon provided $shortcutRequestResult")
  }

  Shortcut(intent, name, icon)
}
