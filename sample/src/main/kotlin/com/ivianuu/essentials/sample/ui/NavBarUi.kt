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

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.hidenavbar.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Given
val navBarHomeItem = HomeItem("Nav bar") { NavBarKey() }

class NavBarKey : Key<Nothing>

@Given
fun navBarUi(
    @Given forceNavBarVisibleState: SampleForceNavBarVisibleState,
    @Given navBarPref: DataStore<NavBarPrefs>,
    @Given navigator: Navigator,
    @Given permissionState: Flow<PermissionState<NavBarPermission>>,
    @Given permissionRequester: PermissionRequester,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
): KeyUi<NavBarKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Nav bar settings") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val navBarPrefs by navBarPref.data.collectAsState(NavBarPrefs())
            // reshow nav bar when leaving the screen
            DisposableEffect(true) {
                onDispose {
                    scope.launch {
                        navBarPref.updateData {
                            copy(hideNavBar = false)
                        }
                    }
                }
            }

            val hasPermission by permissionState.collectAsState(false)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasPermission) {
                        if (navBarPrefs.hideNavBar) {
                            "Nav bar hidden"
                        } else {
                            if (forceNavBarVisibleState.collectAsState().value) {
                                "Nav bar forced shown"
                            } else {
                                "Nav bar shown"
                            }
                        }
                    } else {
                        "Unknown nav bar state"
                    },
                    style = MaterialTheme.typography.h3
                )
            }

            Button(
                onClick = {
                    if (hasPermission) {
                        scope.launch {
                            navBarPref.updateData {
                                copy(hideNavBar = !hideNavBar)
                            }
                        }
                    } else {
                        scope.launch {
                            permissionRequester(listOf(typeKeyOf<NavBarPermission>()))
                        }
                    }
                }
            ) {
                Text("Toggle nav bar")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { forceNavBarVisibleState.value = !forceNavBarVisibleState.value }
            ) { Text("Toggle force nav bar") }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    navigator.push(com.ivianuu.essentials.hidenavbar.ui.NavBarKey())
                }
            ) { Text("Settings") }
        }
    }
}

typealias SampleForceNavBarVisibleState = MutableStateFlow<ForceNavBarVisibleState>

@Given
val sampleForceNavBarVisibleState: @Scoped<AppGivenScope> SampleForceNavBarVisibleState
    get() = MutableStateFlow(false)
