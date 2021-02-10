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

import android.Manifest
import androidx.compose.material.Icon
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.notificationlistener.DefaultNotificationListenerService
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.permission.runtime.RuntimePermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.launch

@HomeItemBinding
@Given
val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey() }

class PermissionsKey

@KeyUiBinding<PermissionsKey>
@GivenFun
@Composable
fun PermissionsScreen(@Given requestPermissions: requestPermissions) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Permissions") }) }
    ) {
        val camera = RuntimePermission(
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
            AmbientContext.current,
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
            AmbientContext.current,
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
        }
    }
}
