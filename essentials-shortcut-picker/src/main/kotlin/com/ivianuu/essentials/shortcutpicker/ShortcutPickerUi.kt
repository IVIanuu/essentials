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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerAction.PickShortcut
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ShortcutPickerKey : Key<Shortcut>

@Given
val shortcutPickerKeyModule = KeyModule<ShortcutPickerKey>()

@Given
fun shortcutPickerUi(
    @Given stateFlow: StateFlow<ShortcutPickerState>,
    @Given dispatch: Collector<ShortcutPickerAction>,
): KeyUi<ShortcutPickerKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.es_title_shortcut_picker)) }
            )
        }
    ) {
        ResourceLazyColumnFor(state.shortcuts) { shortcut ->
            Shortcut(
                info = shortcut,
                onClick = { dispatch(PickShortcut(shortcut)) }
            )
        }
    }
}

@Composable
private fun Shortcut(
    onClick: () -> Unit,
    info: Shortcut
) {
    ListItem(
        leading = {
            Image(
                modifier = Modifier.size(40.dp),
                painter = BitmapPainter(info.icon),
                contentDescription = null
            )
        },
        title = { Text(info.name) },
        onClick = onClick
    )
}

data class ShortcutPickerState(
    val shortcuts: Resource<List<Shortcut>> = Idle,
)

sealed class ShortcutPickerAction {
    data class PickShortcut(val shortcut: Shortcut) : ShortcutPickerAction()
}

@Given
fun shortcutPickerState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: ShortcutPickerState = ShortcutPickerState(),
    @Given actions: Flow<ShortcutPickerAction>,
    @Given activityResultLauncher: ActivityResultLauncher,
    @Given key: ShortcutPickerKey,
    @Given navigator: Collector<NavigationAction>,
    @Given shortcutRepository: ShortcutRepository,
    @Given toaster: Toaster
): @Scoped<KeyUiGivenScope> StateFlow<ShortcutPickerState> = scope.state(initial) {
    reduceResource({ shortcutRepository.getAllShortcuts() }) { copy(shortcuts = it) }
    actions
        .filterIsInstance<PickShortcut>()
        .onEach { action ->
            runCatching {
                val shortcutRequestResult = activityResultLauncher.startActivityForResult(action.shortcut.intent)
                    .data ?: return@onEach
                val shortcut = shortcutRepository.extractShortcut(shortcutRequestResult)
                navigator(Pop(key, shortcut))
            }.onFailure {
                it.printStackTrace()
                toaster.showToast(R.string.es_failed_to_pick_shortcut)
            }
        }
        .launchIn(this)
}

@Given
val shortcutPickerActions: @Scoped<KeyUiGivenScope> MutableSharedFlow<ShortcutPickerAction>
    get() = EventFlow()
