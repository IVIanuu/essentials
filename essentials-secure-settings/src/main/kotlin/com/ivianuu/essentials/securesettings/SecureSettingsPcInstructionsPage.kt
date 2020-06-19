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

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Link
import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.common.navigateOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.UrlRoute
import com.ivianuu.essentials.ui.prefs.ClipboardListItem
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Transient

/**
 * Asks the user for the secure settings permission
 */
@Transient
class SecureSettingsPcInstructionsPage internal constructor(
    private val buildInfo: BuildInfo,
    private val popNavigatorOnceSecureSettingsGranted: PopNavigatorOnceSecureSettingsGranted,
    private val toaster: Toaster
) {

    @Composable
    operator fun invoke() {
        popNavigatorOnceSecureSettingsGranted(toast = false)

        Scaffold(
            topBar = { TopAppBar(title = { Text(R.string.es_title_secure_settings_pc_instructions) }) }
        ) {
            VerticalScroller {
                SecureSettingsHeader(
                    text = stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary)
                )

                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_1) },
                    subtitle = { Text(R.string.es_pref_secure_settings_step_1_summary) }
                )

                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_2) },
                    subtitle = { Text(R.string.es_pref_secure_settings_step_2_summary) }
                )

                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_3) },
                    subtitle = { Text(R.string.es_pref_secure_settings_step_3_summary) }
                )

                ListItem(
                    leading = { Icon(Icons.Default.Link) },
                    title = { Text(R.string.es_pref_secure_settings_link_gadget_hacks_summary) },
                    onClick = navigateOnClick { UrlRoute("https://youtu.be/CDuxcrrWLnY") }
                )

                ListItem(
                    leading = { Icon(Icons.Default.Link) },
                    title = { Text(R.string.es_pref_secure_settings_link_lifehacker_summary) },
                    onClick = navigateOnClick {
                        UrlRoute("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
                    }
                )

                ListItem(
                    leading = { Icon(Icons.Default.Link) },
                    title = { Text(R.string.es_pref_secure_settings_link_xda_summary) },
                    onClick = navigateOnClick {
                        UrlRoute("https://www.xda-developers.com/install-adb-windows-macos-linux/")
                    }
                )

                ClipboardListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_4) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_secure_settings_step_4_summary,
                                buildInfo.packageName
                            )
                        )
                    },
                    clipboardText = {
                        "adb shell pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"
                    }
                )
            }
        }
    }

}
