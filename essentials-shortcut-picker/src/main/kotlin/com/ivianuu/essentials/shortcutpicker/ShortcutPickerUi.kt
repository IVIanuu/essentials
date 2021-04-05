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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

class ShortcutPickerKey : Key<Shortcut>

@Given
val shortcutPickerUi: StateKeyUi<ShortcutPickerKey, ShortcutPickerViewModel,
        ShortcutPickerState> = { viewModel, state ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.es_title_shortcut_picker)) }
                )
            }
        ) {
            ResourceLazyColumnFor(state.shortcuts) { shortcut ->
                ListItem(
                    leading = {
                        Image(
                            modifier = Modifier.size(40.dp),
                            painter = BitmapPainter(shortcut.icon),
                            contentDescription = null
                        )
                    },
                    title = { Text(shortcut.name) },
                    onClick = { viewModel.pickShortcut(shortcut) }
                )
            }
        }
    }

data class ShortcutPickerState(val shortcuts: Resource<List<Shortcut>> = Idle) : State()

@Scoped<KeyUiGivenScope>
@Given
class ShortcutPickerViewModel(
    @Given private val activityResultLauncher: ActivityResultLauncher,
    @Given private val key: ShortcutPickerKey,
    @Given private val navigator: Navigator,
    @Given private val shortcutRepository: ShortcutRepository,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, ShortcutPickerState>,
    @Given private val toaster: Toaster
) : StateFlow<ShortcutPickerState> by store {
    init {
        resourceFlow { emit(shortcutRepository.getAllShortcuts()) }
            .updateIn(store) { copy(shortcuts = it) }
    }
    fun pickShortcut(shortcut: Shortcut) = store.effect {
        runCatching {
            val shortcutRequestResult = activityResultLauncher.startActivityForResult(shortcut.intent)
                .data ?: return@effect
            val finalShortcut = shortcutRepository.extractShortcut(shortcutRequestResult)
            navigator.pop(key, finalShortcut)
        }.onFailure {
            it.printStackTrace()
            toaster.showToast(R.string.es_failed_to_pick_shortcut)
        }
    }
}
