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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsRoute
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.coroutines.CoroutineScopeAmbient
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
import kotlinx.coroutines.launch

val NavBarRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Nav bar settings") }) },
        body = {
            Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val navBarController = inject<NavBarController>()

                    val coroutineScope = CoroutineScopeAmbient.current
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

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        gravity = ContentGravity.Center
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
                            textStyle = MaterialTheme.typography.h3
                        )
                    }

                    val navigator = NavigatorAmbient.current

                    Button(
                        onClick = {
                            if (secureSettingsHelper.canWriteSecureSettings()) {
                                hideNavBar.value = !hideNavBar.value
                            } else {
                                navigator.push(SecureSettingsRoute(true))
                            }
                        }
                    ) {
                        Text("Toggle nav bar")
                    }
                }
            }
        }
    )
}