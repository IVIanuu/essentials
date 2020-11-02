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

import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.*
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlRoute
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Binding
fun CoroutineScope.secureSettingsPcInstructionsStore(
    buildInfo: BuildInfo,
    navigator: Navigator,
    popNavigatorOnceSecureSettingsGranted: popNavigatorOnceSecureSettingsGranted
) = store<SecureSettingsPcInstructionsState, SecureSettingsPcInstructionsAction>(
    SecureSettingsPcInstructionsState(buildInfo.packageName)
) {
    launch { popNavigatorOnceSecureSettingsGranted(false) }
    for (action in this) {
        when (action) {
            ShowGadgetHacksTutorial -> navigator.push(
                UrlRoute("https://youtu.be/CDuxcrrWLnY")
            )
            ShowLifeHackerTutorial -> navigator.push(
                UrlRoute("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
            )
            ShowXdaTutorial -> navigator.push(
                UrlRoute("https://www.xda-developers.com/install-adb-windows-macos-linux/")
            )
        }.exhaustive
    }
}
