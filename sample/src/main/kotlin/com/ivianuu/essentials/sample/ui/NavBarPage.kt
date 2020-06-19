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

import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.onDispose
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarManager
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsPage
import com.ivianuu.essentials.ui.coroutines.compositionCoroutineScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.launch

@Transient
class NavBarPage(
    private val navigator: Navigator,
    private val navBarManager: NavBarManager,
    private val secureSettingsHelper: SecureSettingsHelper,
    private val secureSettingsPage: SecureSettingsPage
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Nav bar settings") }) }
        ) {
            Column(
                modifier = Modifier.center(),
                verticalArrangement = Arrangement.Center,
                horizontalGravity = Alignment.CenterHorizontally
            ) {
                val scope = compositionCoroutineScope()
                fun updateNavBarState(navBarHidden: Boolean) {
                    scope.launch {
                        navBarManager.setNavBarConfig(
                            NavBarConfig(navBarHidden)
                        )
                    }
                }

                val hideNavBar = state { false }

                onCommit(hideNavBar.value) { updateNavBarState(hideNavBar.value) }

                // reshow nav bar when exiting the screen
                onDispose { updateNavBarState(false) }

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
                        style = MaterialTheme.typography.h3
                    )
                }

                Button(
                    onClick = {
                        if (secureSettingsHelper.canWriteSecureSettings()) {
                            hideNavBar.value = !hideNavBar.value
                        } else {
                            navigator.push { secureSettingsPage() }
                        }
                    }
                ) {
                    Text("Toggle nav bar")
                }
            }
        }
    }
}
