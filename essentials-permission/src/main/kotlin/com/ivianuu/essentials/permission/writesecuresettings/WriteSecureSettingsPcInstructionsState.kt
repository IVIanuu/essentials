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

package com.ivianuu.essentials.permission.writesecuresettings

import com.ivianuu.essentials.clipboard.ClipboardAction
import com.ivianuu.essentials.clipboard.ClipboardAction.UpdateClipboard
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.CopyAdbCommand
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenGadgetHacksTutorial
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenLifeHackerTutorial
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenXdaTutorial
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UiStateBinding
@Given
fun writeSecureSettingsPcInstructionsState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial WriteSecureSettingsPcInstructionsState,
    @Given actions: Actions<WriteSecureSettingsPcInstructionsAction>,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given clipboard: DispatchAction<ClipboardAction>,
    @Given key: WriteSecureSettingsPcInstructionsKey,
    @Given permissionStateFactory: PermissionStateFactory
): StateFlow<WriteSecureSettingsPcInstructionsState> = scope.state(initial) {
    launch {
        val state = permissionStateFactory(listOf(key.permissionKey))
        while (coroutineContext.isActive) {
            if (state.first()) {
                navigator(PopTop())
                break
            }
            delay(200)
        }
    }

    actions
        .filterIsInstance<CopyAdbCommand>()
        .onEach { clipboard(UpdateClipboard(currentState().secureSettingsAdbCommand)) }
        .launchIn(this)

    actions
        .filterIsInstance<OpenGadgetHacksTutorial>()
        .onEach {
            navigator(
                Push(
                    UrlKey("https://youtu.be/CDuxcrrWLnY")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenLifeHackerTutorial>()
        .onEach {
            navigator(
                Push(
                    UrlKey("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenXdaTutorial>()
        .onEach {
            navigator(
                Push(
                    UrlKey("https://www.xda-developers.com/install-adb-windows-macos-linux/")
                )
            )
        }
        .launchIn(this)
}
