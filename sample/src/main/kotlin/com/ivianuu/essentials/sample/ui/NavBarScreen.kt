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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarManager
import com.ivianuu.essentials.permission.PermissionBinding
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.ui.PermissionUiMetadata
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.coroutines.rememberRetainedCoroutinesScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.launch

@HomeItemBinding @Given
val navBarHomeItem = HomeItem("Nav bar") { NavBarKey() }

class NavBarKey

@KeyUiBinding<NavBarKey>
@Given
fun navBarKeyUi(
    @Given navBarManager: NavBarManager,
    @Given permissionState: PermissionState<NavBarSecureSettingsPermission>,
    @Given permissionRequester: PermissionRequester,
): KeyUi = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Nav bar settings") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val scope = rememberRetainedCoroutinesScope()
            fun updateNavBarState(navBarHidden: Boolean) {
                scope.launch {
                    navBarManager.setNavBarConfig(
                        NavBarConfig(navBarHidden)
                    )
                }
            }

            var hideNavBar by remember { mutableStateOf(false) }
            SideEffect { updateNavBarState(hideNavBar) }

            // reshow nav bar when leaving the screen
            DisposableEffect(true) {
                onDispose { updateNavBarState(false) }
            }

            val hasPermission by permissionState.collectAsState(false)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasPermission) {
                        if (hideNavBar) {
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
                    if (hasPermission) {
                        hideNavBar = !hideNavBar
                    } else {
                        scope.launch {
                            permissionRequester(listOf(typeKeyOf<NavBarSecureSettingsPermission>()))
                        }
                    }
                }
            ) {
                Text("Toggle nav bar")
            }
        }
    }
}

@PermissionBinding
@Given
object NavBarSecureSettingsPermission : WriteSecureSettingsPermission

@Given
val navBarSecureSettingsPermissionMetadata = PermissionUiMetadata<NavBarSecureSettingsPermission>(
    title = "Write secure settings",
    desc = "This is a desc",
    icon = { Icon(Icons.Default.Menu, null) }
)
