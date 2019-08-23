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

import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.coroutineScope
import com.ivianuu.compose.common.launchOnActive
import com.ivianuu.compose.common.withHandlers
import com.ivianuu.essentials.ui.changehandler.VerticalFadeChangeHandler
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.ListItem
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.navigateOnClick
import com.ivianuu.essentials.ui.prefs.Prefs
import com.ivianuu.essentials.util.Toaster
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Asks the user for the secure settings permission
 */
fun SecureSettingsRoute(
    showHideNavBarHint: Boolean = false
) = Route {
    val secureSettingsHelper = inject<SecureSettingsHelper>()
    val toaster = inject<Toaster>()
    val navigator = ambient(NavigatorAmbient)

    launchOnActive {
        while (true) {
            if (secureSettingsHelper.canWriteSecureSettings()) {
                toaster.toast(R.string.es_secure_settings_permission_granted)
                navigator.pop(true)
                break
            }
            delay(100)
        }
    }

    Prefs {
        Scaffold(
            appBar = { AppBar(titleRes = R.string.es_title_secure_settings) },
            content = {
                RecyclerView {
                    ListItem(
                        textRes = if (showHideNavBarHint) R.string.es_pref_secure_settings_header_hide_nav_bar_summary
                        else R.string.es_pref_secure_settings_header_summary
                    )

                    ListItem(
                        titleRes = R.string.es_pref_use_pc,
                        textRes = R.string.es_pref_use_pc_summary,
                        onClick = navigateOnClick {
                            SecureSettingsInstructionsRoute()
                                .withHandlers(handler = VerticalFadeChangeHandler())
                        }
                    )

                    val coroutineScope = coroutineScope

                    ListItem(
                        titleRes = R.string.es_pref_use_root,
                        textRes = R.string.es_pref_use_root_summary,
                        onClick = {
                            coroutineScope.launch {
                                if (!secureSettingsHelper.grantWriteSecureSettingsViaRoot()) {
                                    toaster.toast(R.string.es_secure_settings_no_root)
                                }
                            }
                        }
                    )
                }
            }
        )
    }
}