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

import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerAction.PickShortcut
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.popWithResult
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UiStateBinding
@Given
fun shortcutPickerState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial ShortcutPickerState = ShortcutPickerState(),
    @Given actions: Actions<ShortcutPickerAction>,
    @Given activityResultLauncher: ActivityResultLauncher,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given shortcutRepository: ShortcutRepository,
    @Given toaster: Toaster
): StateFlow<ShortcutPickerState> = scope.state(initial) {
    reduceResource({ shortcutRepository.getAllShortcuts() }) { copy(shortcuts = it) }
    actions
        .filterIsInstance<PickShortcut>()
        .onEach { action ->
            runKatching {
                val shortcutRequestResult = activityResultLauncher.startActivityForResult(action.shortcut.intent)
                    .data ?: return@onEach
                val shortcut = shortcutRepository.extractShortcut(shortcutRequestResult)
                navigator.popWithResult(shortcut)
            }
                .onFailure {
                    it.printStackTrace()
                    toaster.showToast(R.string.es_failed_to_pick_shortcut)
                }
        }
        .launchIn(this)
}
