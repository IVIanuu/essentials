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

import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.OpenGadgetHacksTutorial
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.OpenLifeHackerTutorial
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.OpenXdaTutorial
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlRoute
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiStoreBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@UiStoreBinding
fun CoroutineScope.SecureSettingsPcInstructionsStore(
    initial: @Initial SecureSettingsPcInstructionsState,
    navigator: Navigator,
    popNavigatorOnceSecureSettingsGranted: popNavigatorOnceSecureSettingsGranted
) = store<SecureSettingsPcInstructionsState, SecureSettingsPcInstructionsAction>(initial) {
    launch { popNavigatorOnceSecureSettingsGranted(false) }
    for (action in this) {
        when (action) {
            OpenGadgetHacksTutorial -> navigator.push(
                UrlRoute("https://youtu.be/CDuxcrrWLnY")
            )
            OpenLifeHackerTutorial -> navigator.push(
                UrlRoute("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
            )
            OpenXdaTutorial -> navigator.push(
                UrlRoute("https://www.xda-developers.com/install-adb-windows-macos-linux/")
            )
        }
    }
}
