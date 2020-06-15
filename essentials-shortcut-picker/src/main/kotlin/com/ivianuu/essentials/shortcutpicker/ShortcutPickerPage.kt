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
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.size
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.activityresult.ActivityResultManager
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.mvrx.currentState
import com.ivianuu.essentials.ui.common.RenderAsyncList
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.rememberRetained
import com.ivianuu.essentials.ui.image.Image
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.launch

@Transient
class ShortcutPickerPage internal constructor(
    private val viewModelFactory: @Provider () -> ShortcutPickerViewModel
) {
    @Composable
    operator fun invoke(title: String? = null) {
        val viewModel = rememberRetained { viewModelFactory() }
        Scaffold(
            topAppBar = {
                TopAppBar(title = {
                    Text(
                        title ?: stringResource(R.string.es_title_shortcut_picker)
                    )
                })
            },
            body = {
                RenderAsyncList(state = viewModel.currentState.shortcuts) { shortcut ->
                    Shortcut(info = shortcut, onClick = { viewModel.shortcutClicked(shortcut) })
                }
            }
        )
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
    private val activityResultManager: ActivityResultManager,
    private val navigator: Navigator,
    private val shortcutStore: ShortcutStore
) : MvRxViewModel<ShortcutPickerState>(ShortcutPickerState()) {
    init {
        scope.execute(
            block = { shortcutStore.getShortcuts() },
            reducer = { copy(shortcuts = it) }
        )
    }

    fun shortcutClicked(info: Shortcut) {
        scope.launch {
            try {
                val shortcutRequestResult = activityResultManager.startForResult(
                    info.intent
                ).data ?: return@launch

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
