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

package com.ivianuu.essentials.securesettings

import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.navigator
import com.ivianuu.compose.launchOnActive
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.injekt.inject
import kotlinx.coroutines.delay

/**
 * Asks the user for the secure settings permission
 */
fun SecureSettingsInstructionsRoute() = Route {
    val secureSettingsHelper = inject<SecureSettingsHelper>()
    val navigator = navigator

    launchOnActive {
        while (true) {
            if (secureSettingsHelper.canWriteSecureSettings()) {
                navigator.pop()
                break
            }
            delay(500)
        }
    }

    Scaffold(
        appBar = { AppBar(titleRes = R.string.es_title_secure_settings_pc_instructions) },
        content = {
            RecyclerView {
                /*ListItem(
                    id = "secure_settings_header",
                    textRes = R.string.es_pref_secure_settings_pc_instructions_header_summary
                )

                IntentListItem(
                    id = "secure_settings_step_1",
                    titleRes = R.string.es_pref_secure_settings_step_1,
                    textRes = R.string.es_pref_secure_settings_step_1_summary,
                    intent = { Intent(Settings.ACTION_DEVICE_INFO_SETTINGS) }
                )

                IntentListItem(
                    id = "secure_settings_step_2",
                    titleRes = R.string.es_pref_secure_settings_step_2,
                    textRes = R.string.es_pref_secure_settings_step_2_summary,
                    intent = { Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) }
                )

                ListItem(
                    id = "secure_settings_step_3",
                    titleRes = R.string.es_pref_secure_settings_step_3,
                    textRes = R.string.es_pref_secure_settings_step_3_summary
                )

                UrlListItem(
                    id = "secure_settings_link_gadget_hacks",
                    iconRes = R.drawable.es_ic_link,
                    textRes = R.string.es_pref_secure_settings_link_gadget_hacks_summary,
                    url = { "https://youtu.be/CDuxcrrWLnY" }
                )

                UrlListItem(
                    id = "secure_settings_link_lifehacker",
                    iconRes = R.drawable.es_ic_link,
                    textRes = R.string.es_pref_secure_settings_link_lifehacker_summary,
                    url = { "https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378" }
                )

                UrlListItem(
                    id = "secure_settings_link_xda",
                    iconRes = R.drawable.es_ic_link,
                    textRes = R.string.es_pref_secure_settings_link_xda_summary,
                    url = { "https://www.xda-developers.com/install-adb-windows-macos-linux/" }
                )

                ClipboardListItem(
                    id = "secure_settings_step_4",
                    titleRes = R.string.es_pref_secure_settings_step_4,
                    text = string(R.string.es_pref_secure_settings_step_4_summary, buildInfo.packageName),
                    clip = "adb shell pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"
                )
*/
            }
        }
    )
}