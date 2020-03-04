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
import androidx.compose.Pivotal
import androidx.ui.graphics.ImageAsset
import androidx.ui.res.stringResource
import com.ivianuu.essentials.activityresult.ActivityResult
import com.ivianuu.essentials.activityresult.ActivityResultRoute
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.mvrx.injectMvRxViewModel
import com.ivianuu.essentials.ui.common.RenderAsyncList
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.image.AvatarSize
import com.ivianuu.essentials.ui.image.Image
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.launch

fun ShortcutPickerRoute(
    title: String? = null
) = Route {
    val viewModel = injectMvRxViewModel<ShortcutPickerViewModel>()
    SimpleScreen(title = title ?: stringResource(R.string.es_title_shortcut_picker)) {
        RenderAsyncList(state = viewModel.state.shortcuts) { info ->
            ShortcutInfo(info = info, onClick = { viewModel.infoClicked(info) })
        }
    }
}

@Composable
private fun ShortcutInfo(
    @Pivotal info: ShortcutInfo,
    onClick: () -> Unit
) {
    ListItem(
        leading = { Image(image = info.icon, size = AvatarSize) },
        title = { Text(info.name) },
        onClick = onClick
    )
}

@Factory
private class ShortcutPickerViewModel(
    private val navigator: NavigatorState,
    private val packageManager: PackageManager
) : MvRxViewModel<ShortcutPickerState>(
    ShortcutPickerState()
) {
    init {
        scope.coroutineScope.execute(
            block = {
                val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
                packageManager.queryIntentActivities(shortcutsIntent, 0)
                    .mapNotNull { resolveInfo ->
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
                    .sortedBy { it.name }
            },
            reducer = { copy(shortcuts = it) }
        )
    }

    fun infoClicked(info: ShortcutInfo) {
        scope.coroutineScope.launch {
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
                        val iconResId =
                            resources.getIdentifier(iconResource.resourceName, null, null)
                        resources.getDrawable(iconResId).toImageAsset()
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
    val shortcuts: Async<List<ShortcutInfo>> = Uninitialized
)

@Immutable
internal data class ShortcutInfo(
    val intent: Intent,
    val name: String,
    val icon: ImageAsset
)
