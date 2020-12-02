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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarManager
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.coroutines.rememberRetainedCoroutinesScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.launch

@HomeItemBinding("Nav bar")
class NavBarKey

@KeyUiBinding<NavBarKey>
@FunBinding
@Composable
fun NavBarPage(
    hasPermissions: hasPermissions,
    navBarManager: NavBarManager,
    requestPermissions: requestPermissions,
) {
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

            var hideNavBar by rememberState { false }
            onCommit(hideNavBar) { updateNavBarState(hideNavBar) }

            // reshow nav bar when exiting the screen
            onDispose { updateNavBarState(false) }

            val secureSettingsPermission = remember {
                WriteSecureSettingsPermission(
                    Permission.Title to "Write secure settings",
                    Permission.Desc to "This is a desc",
                    Permission.Icon to { Icon(Icons.Default.Menu) }
                )
            }

            val hasPermission by remember { hasPermissions(listOf(secureSettingsPermission)) }
                .collectAsState(false)

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
                            requestPermissions(listOf(secureSettingsPermission))
                        }
                    }
                }
            ) {
                Text("Toggle nav bar")
            }
        }
    }
}
