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

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.withContext

typealias getAllShortcuts = suspend () -> List<Shortcut>
@Binding
fun getAllShortcuts(
    ioDispatcher: IODispatcher,
    packageManager: PackageManager,
): getAllShortcuts = {
    withContext(ioDispatcher) {
        val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
        packageManager.queryIntentActivities(shortcutsIntent, 0)
            .parallelMap { resolveInfo ->
                try {
                    Shortcut(
                        intent = Intent().apply {
                            action = Intent.ACTION_CREATE_SHORTCUT
                            component = ComponentName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name
                            )
                        },
                        name = resolveInfo.loadLabel(packageManager).toString(),
                        icon = resolveInfo.loadIcon(packageManager).toImageAsset()
                    )
                } catch (t: Throwable) {
                    null
                }
            }
            .filterNotNull()
            .sortedBy { it.name }
    }
}

typealias extractShortcut = suspend (Intent) -> Shortcut
@Binding
fun extractShortcut(
    defaultDispatcher: DefaultDispatcher,
    packageManager: PackageManager
): extractShortcut = { shortcutRequestResult ->
    withContext(defaultDispatcher) {
        val intent =
            shortcutRequestResult.getParcelableExtra<Intent>(Intent.EXTRA_SHORTCUT_INTENT)!!
        val name = shortcutRequestResult.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)!!
        val bitmapIcon =
            shortcutRequestResult.getParcelableExtra<Bitmap>(Intent.EXTRA_SHORTCUT_ICON)
        val iconResource =
            shortcutRequestResult.getParcelableExtra<Intent.ShortcutIconResource>(Intent.EXTRA_SHORTCUT_ICON_RESOURCE)

        val icon = when {
            bitmapIcon != null -> bitmapIcon.toImageAsset()
            iconResource != null -> {
                val resources =
                    packageManager.getResourcesForApplication(iconResource.packageName)
                val id =
                    resources.getIdentifier(iconResource.resourceName, null, null)
                resources.getDrawable(id).toImageAsset()
            }
            else -> error("no icon provided $shortcutRequestResult")
        }

        Shortcut(intent, name, icon)
    }
}
