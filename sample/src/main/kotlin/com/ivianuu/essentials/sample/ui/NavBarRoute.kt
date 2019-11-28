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

import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.secureSettingsRoute
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.onCommit
import com.ivianuu.essentials.ui.compose.core.onDispose
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import kotlinx.coroutines.launch

val navBarRoute = composeControllerRoute(
    options = controllerRouteOptions().fade()
) {
    Scaffold(
        topAppBar = { EsTopAppBar(title = "Nav bar settings") },
        body = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val navBarController = inject<NavBarController>()

                    val coroutineScope = coroutineScope()
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
                        modifier = ExpandedWidth,
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
                            style = MaterialTheme.typography()().h3
                        )
                    }

                    val navigator = inject<Navigator>()

                    Button(
                        "Toggle nav bar",
                        onClick = {
                            if (secureSettingsHelper.canWriteSecureSettings()) {
                                hideNavBar.value = !hideNavBar.value
                            } else {
                                navigator.push(secureSettingsRoute(true))
                            }
                        }
                    )
                }
            }
        }
    )
}