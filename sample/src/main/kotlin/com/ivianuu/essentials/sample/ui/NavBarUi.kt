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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.android.prefs.PrefAction
import com.ivianuu.essentials.android.prefs.dispatchUpdate
import com.ivianuu.essentials.hidenavbar.ForceNavBarVisibleState
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Given
val navBarHomeItem = HomeItem("Nav bar") { NavBarKey() }

class NavBarKey : Key<Nothing>

@Given
val navBarKeyModule = KeyModule<NavBarKey>()

@Given
fun navBarUi(
    @Given forceNavBarVisibleState: SampleForceNavBarVisibleState,
    @Given navBarPrefsFlow: Flow<NavBarPrefs>,
    @Given navBarPrefsUpdater: Collector<PrefAction<NavBarPrefs>>,
    @Given navigator: Collector<NavigationAction>,
    @Given permissionState: Flow<PermissionState<NavBarPermission>>,
    @Given permissionRequester: PermissionRequester
): KeyUi<NavBarKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Nav bar settings") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val navBarPrefs by navBarPrefsFlow.collectAsState(NavBarPrefs())
            // reshow nav bar when leaving the screen
            DisposableEffect(true) {
                onDispose {
                    navBarPrefsUpdater.dispatchUpdate {
                        copy(hideNavBar = false)
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

            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    if (hasPermission) {
                        navBarPrefsUpdater.dispatchUpdate {
                            copy(hideNavBar = !hideNavBar)
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
                    navigator(Push(com.ivianuu.essentials.hidenavbar.ui.NavBarKey()))
                }
            ) { Text("Settings") }
        }
    }
}

typealias SampleForceNavBarVisibleState = MutableStateFlow<ForceNavBarVisibleState>

@Given
fun sampleForceNavBarVisibleState(): @Scoped<AppGivenScope> SampleForceNavBarVisibleState =
    MutableStateFlow(false)
