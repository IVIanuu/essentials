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

import android.content.Intent
import android.provider.Settings
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.launchOnActive
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.Icon
import com.ivianuu.essentials.ui.compose.ListItem
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.Text
import com.ivianuu.essentials.ui.compose.copyToClipboardOnClick
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.openUrlOnClick
import com.ivianuu.essentials.ui.compose.sendIntentOnClick
import com.ivianuu.essentials.util.BuildInfo
import kotlinx.coroutines.delay

/**
 * Asks the user for the secure settings permission
 */
fun SecureSettingsInstructionsRoute() = Route {
    val secureSettingsHelper = inject<SecureSettingsHelper>()
    val navigator = ambient(NavigatorAmbient)

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
                ListItem(text = { Text(textRes = R.string.es_pref_secure_settings_pc_instructions_header_summary) })

                ListItem(
                    title = { Text(textRes = R.string.es_pref_secure_settings_step_1) },
                    text = { Text(textRes = R.string.es_pref_secure_settings_step_1_summary) },
                    onClick = sendIntentOnClick { Intent(Settings.ACTION_DEVICE_INFO_SETTINGS) }
                )

                ListItem(
                    title = { Text(textRes = R.string.es_pref_secure_settings_step_2) },
                    text = { Text(textRes = R.string.es_pref_secure_settings_step_2_summary) },
                    onClick = sendIntentOnClick { Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) }
                )

                ListItem(
                    title = { Text(textRes = R.string.es_pref_secure_settings_step_3) },
                    text = { Text(textRes = R.string.es_pref_secure_settings_step_3_summary) }
                )

                fun ComponentComposition.LinkIcon() {
                    Icon(imageRes = R.drawable.es_ic_link)
                }

                ListItem(
                    leading = { LinkIcon() },
                    text = { Text(textRes = R.string.es_pref_secure_settings_link_gadget_hacks_summary) },
                    onClick = openUrlOnClick { "https://youtu.be/CDuxcrrWLnY" }
                )

                ListItem(
                    leading = { LinkIcon() },
                    text = { Text(textRes = R.string.es_pref_secure_settings_link_lifehacker_summary) },
                    onClick = openUrlOnClick { "https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378" }
                )

                ListItem(
                    leading = { LinkIcon() },
                    text = { Text(textRes = R.string.es_pref_secure_settings_link_xda_summary) },
                    onClick = openUrlOnClick { "https://www.xda-developers.com/install-adb-windows-macos-linux/" }
                )

                val buildInfo = inject<BuildInfo>()
                ListItem(
                    title = { Text(textRes = R.string.es_pref_secure_settings_step_4) },
                    text = { Text(textRes = R.string.es_pref_secure_settings_step_4_summary) },
                    onClick = copyToClipboardOnClick {
                        "adb shell pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"
                    }
                )
            }
        }
    )
}