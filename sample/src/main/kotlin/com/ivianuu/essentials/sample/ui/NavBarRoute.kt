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

package com.ivianuu.essentials.sample.ui

import androidx.compose.onCommit
import androidx.compose.onDispose
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.layout.Center
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutExpandedWidth
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsRoute
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.coroutines.coroutineScope
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import kotlinx.coroutines.launch

val NavBarRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = "Nav bar settings") },
        body = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val navBarController = inject<NavBarController>()

                    val coroutineScope = coroutineScope
                    fun updateNavBarState(navBarHidden: Boolean) {
                        coroutineScope.launch {
                            navBarController.setNavBarConfig(
                                NavBarConfig(navBarHidden)
                            )
                        }
                    }

                    val hideNavBar = state { false }

                    onCommit(hideNavBar.value) { updateNavBarState(hideNavBar.value) }

                    // reshow nav bar when exiting the screen
                    onDispose { updateNavBarState(false) }

                    val secureSettingsHelper = inject<SecureSettingsHelper>()

                    Container(
                        modifier = LayoutExpandedWidth,
                        alignment = Alignment.Center
                    ) {
                        Text(
                            text = if (secureSettingsHelper.canWriteSecureSettings()) {
                                if (hideNavBar.value) {
                                    "Nav bar hidden"
                                } else {
                                    "Nav bar shown"
                                }
                            } else {
                                "Unknown nav bar state"
                            },
                            style = MaterialTheme.typography().h3
                        )
                    }

                    val navigator = navigator

                    Button(
                        "Toggle nav bar",
                        onClick = {
                            if (secureSettingsHelper.canWriteSecureSettings()) {
                                hideNavBar.value = !hideNavBar.value
                            } else {
                                navigator.push(SecureSettingsRoute(true))
                            }
                        }
                    )
                }
            }
        }
    )
}