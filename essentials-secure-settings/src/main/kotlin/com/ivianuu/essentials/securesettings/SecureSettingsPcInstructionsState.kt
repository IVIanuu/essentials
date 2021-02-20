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

package com.ivianuu.essentials.securesettings

import com.ivianuu.essentials.clipboard.ClipboardAction
import com.ivianuu.essentials.clipboard.ClipboardAction.UpdateClipboard
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.CopyAdbCommand
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.OpenGadgetHacksTutorial
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.OpenLifeHackerTutorial
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.OpenXdaTutorial
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@UiStateBinding
@Given
fun secureSettingsPcInstructionsState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial SecureSettingsPcInstructionsState,
    @Given actions: Actions<SecureSettingsPcInstructionsAction>,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given clipboard: DispatchAction<ClipboardAction>,
    @Given permission: SecureSettingsPermission,
    @Given toaster: Toaster
): StateFlow<SecureSettingsPcInstructionsState> = scope.state(initial) {
    launch { navigator.popOnceSecureSettingsPermissionIsGranted(false, permission, toaster) }

    actions
        .filterIsInstance<CopyAdbCommand>()
        .onEach { clipboard(UpdateClipboard(currentState().secureSettingsAdbCommand)) }
        .launchIn(this)

    actions
        .filterIsInstance<OpenGadgetHacksTutorial>()
        .onEach {
            navigator(
                NavigationAction.Push(
                    UrlKey("https://youtu.be/CDuxcrrWLnY")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenLifeHackerTutorial>()
        .onEach {
            navigator(
                NavigationAction.Push(
                    UrlKey("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenXdaTutorial>()
        .onEach {
            navigator(
                NavigationAction.Push(
                    UrlKey("https://www.xda-developers.com/install-adb-windows-macos-linux/")
                )
            )
        }
        .launchIn(this)
}
