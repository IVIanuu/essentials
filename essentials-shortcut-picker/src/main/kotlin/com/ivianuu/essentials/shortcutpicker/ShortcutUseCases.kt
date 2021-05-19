/*
 * Copyright 2020 Manuel Wrage
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

import android.content.*
import android.content.pm.*
import android.graphics.*
import androidx.core.graphics.drawable.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*

typealias GetAllShortcutsUseCase = suspend () -> List<Shortcut>

@Provide fun getAllShortcutsUseCase(
  dispatcher: IODispatcher,
  packageManager: PackageManager
): GetAllShortcutsUseCase = {
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
            icon = resolveInfo.loadIcon(packageManager).toBitmap().toImageBitmap()
          )
        }.getOrNull()
      }
      .filterNotNull()
      .sortedBy { it.name }
  }
}

typealias ExtractShortcutUseCase = (Intent) -> Shortcut

@Provide fun extractShortcutUseCase(
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
    bitmapIcon != null -> bitmapIcon.toImageBitmap()
    iconResource != null -> {
      val resources =
        packageManager.getResourcesForApplication(iconResource.packageName)
      val id =
        resources.getIdentifier(iconResource.resourceName, null, null)
      resources.getDrawable(id).toBitmap().toImageBitmap()
    }
    else -> error("no icon provided $shortcutRequestResult")
  }

  Shortcut(intent, name, icon)
}
