/*
 * Copyright 2019 Manuel Wrage
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
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.core.paint
import androidx.ui.foundation.Box
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.preferredSize
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.activityresult.ActivityResult
import com.ivianuu.essentials.activityresult.ActivityResultRoute
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.common.RenderAsyncList
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.viewmodel.injectViewModel
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.launch

fun ShortcutPickerRoute(
    title: String? = null
) = Route {
    val viewModel = injectViewModel<ShortcutPickerViewModel>()
    SimpleScreen(title = title ?: stringResource(R.string.es_title_shortcut_picker)) {
        RenderAsyncList(state = viewModel.state.shortcuts) { info ->
            ShortcutInfo(info = info, onClick = { viewModel.infoClicked(info) })
        }
    }
}

@Composable
private fun ShortcutInfo(
    onClick: () -> Unit,
    info: ShortcutInfo
) {
    key(info) {
        ListItem(
            leading = {
                Box(
                    modifier = Modifier.preferredSize(size = 40.dp)
                        .paint(ImagePainter(info.icon))
                )
            },
            title = { Text(info.name) },
            onClick = onClick
        )
    }
}

@Transient
internal class ShortcutPickerViewModel(
    private val navigator: Navigator,
    private val packageManager: PackageManager
) : MvRxViewModel<ShortcutPickerState>(ShortcutPickerState()) {
    init {
        coroutineScope.execute(
            block = {
                val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
                packageManager.queryIntentActivities(shortcutsIntent, 0)
                    .parallelMap { resolveInfo ->
                        try {
                            ShortcutInfo(
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
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .filterNotNull()
                    .sortedBy { it.name }
            },
            reducer = { copy(shortcuts = it) }
        )
    }

    fun infoClicked(info: ShortcutInfo) {
        coroutineScope.launch {
            try {
                val shortcutRequestResult = navigator.push<ActivityResult>(
                    ActivityResultRoute(
                        intent = info.intent
                    )
                )?.data ?: return@launch
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

                val shortcut = Shortcut(
                    intent = intent,
                    name = name,
                    icon = icon
                )

                navigator.popTop(result = shortcut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Immutable
internal data class ShortcutPickerState(
    val shortcuts: Async<List<ShortcutInfo>> = Uninitialized()
)

@Immutable
internal data class ShortcutInfo(
    val intent: Intent,
    val name: String,
    val icon: ImageAsset
)
