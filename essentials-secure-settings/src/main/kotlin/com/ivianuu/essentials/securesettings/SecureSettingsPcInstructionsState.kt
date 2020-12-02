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
import com.ivianuu.essentials.clipboard.ClipboardAction.*
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.*
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.store.UiStateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@UiStateBinding
fun secureSettingsPcInstructionsState(
    scope: CoroutineScope,
    initial: @Initial SecureSettingsPcInstructionsState,
    actions: Actions<SecureSettingsPcInstructionsAction>,
    dispatchNavigationAction: DispatchAction<NavigationAction>,
    dispatchClipboardAction: DispatchAction<ClipboardAction>,
    popNavigatorOnceSecureSettingsGranted: popNavigatorOnceSecureSettingsGranted,
) = scope.state(initial) {
    launch { popNavigatorOnceSecureSettingsGranted(false) }

    actions
        .filterIsInstance<CopyAdbCommand>()
        .onEach { dispatchClipboardAction(UpdateClipboard(currentState().secureSettingsAdbCommand)) }
        .launchIn(this)

    actions
        .filterIsInstance<OpenGadgetHacksTutorial>()
        .onEach {
            dispatchNavigationAction(
                NavigationAction.Push(
                    UrlKey("https://youtu.be/CDuxcrrWLnY")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenLifeHackerTutorial>()
        .onEach {
            dispatchNavigationAction(
                NavigationAction.Push(
                    UrlKey("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenXdaTutorial>()
        .onEach {
            dispatchNavigationAction(
                NavigationAction.Push(
                    UrlKey("https://www.xda-developers.com/install-adb-windows-macos-linux/")
                )
            )
        }
        .launchIn(this)
}
