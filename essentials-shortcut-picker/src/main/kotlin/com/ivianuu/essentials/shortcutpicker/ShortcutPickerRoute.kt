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

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.collectAsState
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.size
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.activityresult.ActivityResult
import com.ivianuu.essentials.activityresult.ActivityResultRoute
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.common.RenderAsyncList
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.image.Image
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
        RenderAsyncList(state = viewModel.state.collectAsState().value.shortcuts) { shortcut ->
            Shortcut(info = shortcut, onClick = { viewModel.shortcutClicked(shortcut) })
        }
    }
}

@Composable
private fun Shortcut(
    onClick: () -> Unit,
    info: Shortcut
) {
    key(info) {
        ListItem(
            leading = {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = ImagePainter(info.icon)
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
    private val shortcutStore: ShortcutStore
) : MvRxViewModel<ShortcutPickerState>(ShortcutPickerState()) {
    init {
        coroutineScope.execute(
            block = { shortcutStore.getShortcuts() },
            reducer = { copy(shortcuts = it) }
        )
    }

    fun shortcutClicked(info: Shortcut) {
        coroutineScope.launch {
            try {
                val shortcutRequestResult = navigator.push<ActivityResult>(
                    ActivityResultRoute(
                        intent = info.intent
                    )
                ).await()?.data ?: return@launch

                val shortcut = shortcutStore.getShortcut(shortcutRequestResult)

                navigator.popTop(result = shortcut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Immutable
internal data class ShortcutPickerState(
    val shortcuts: Async<List<Shortcut>> = Uninitialized()
)
