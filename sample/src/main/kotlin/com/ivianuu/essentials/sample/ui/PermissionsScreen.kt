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

import androidx.compose.material.Text
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.Given

@HomeItemBinding
@Given
val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey() }

class PermissionsKey

@KeyUiBinding<PermissionsKey>
@Given
fun permissionKeyUi(@Given permissionRequester: PermissionRequester): KeyUi = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Permissions") }) }
    ) {
        TODO()
        /*val camera = RuntimePermission(
            Manifest.permission.CAMERA,
            Permission.Title to "Camera",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val phone = RuntimePermission(
            Manifest.permission.CALL_PHONE,
            Permission.Title to "Call phone",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val accessibility = AccessibilityServicePermission(
            DefaultAccessibilityService::class,
            Permission.Title to "Accessibility",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val notificationListener = NotificationListenerPermission(
            DefaultNotificationListenerService::class,
            Permission.Title to "Notification listener",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val systemOverlay = SystemOverlayPermission(
            LocalContext.current,
            Permission.Title to "System overlay",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val writeSecureSettings = WriteSecureSettingsPermission(
            Permission.Title to "Write secure settings",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val writeSettings = WriteSettingsPermission(
            LocalContext.current,
            Permission.Title to "Write settings",
            Permission.Desc to "This is a desc",
            Permission.Icon to { Icon(Icons.Default.Menu, null) }
        )

        val scope = rememberCoroutineScope()
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    requestPermissions(
                        listOf(
                            camera,
                            phone,
                            accessibility,
                            notificationListener,
                            systemOverlay,
                            writeSecureSettings,
                            writeSettings
                        )
                    )
                }
            }
        ) {
            Text("Request")
        }*/
    }
}
