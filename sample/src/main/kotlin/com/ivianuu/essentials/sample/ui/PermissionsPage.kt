/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed with in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import android.Manifest
import androidx.compose.Composable
import androidx.compose.rememberCoroutineScope
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Menu
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.billing.PurchaseManager
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
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.launch

@Reader
@Composable
fun PermissionsPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Permissions") }) }
    ) {
        val camera = RuntimePermission(
            Manifest.permission.CAMERA,
            Permission.Title withValue "Camera",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val phone = RuntimePermission(
            Manifest.permission.CALL_PHONE,
            Permission.Title withValue "Call phone",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val accessibility = AccessibilityServicePermission(
            DefaultAccessibilityService::class,
            Permission.Title withValue "Accessibility",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val notificationListener = NotificationListenerPermission(
            DefaultNotificationListenerService::class,
            Permission.Title withValue "Notification listener",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val systemOverlay = SystemOverlayPermission(
            ContextAmbient.current,
            Permission.Title withValue "System overlay",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val writeSecureSettings = WriteSecureSettingsPermission(
            Permission.Title withValue "Write secure settings",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val writeSettings = WriteSettingsPermission(
            ContextAmbient.current,
            Permission.Title withValue "Write settings",
            Permission.Desc withValue "This is a desc",
            Permission.Icon withValue { Icon(Icons.Default.Menu) }
        )

        val scope = rememberCoroutineScope()
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    requestPermissions(
                        camera,
                        phone,
                        accessibility,
                        notificationListener,
                        systemOverlay,
                        writeSecureSettings,
                        writeSettings
                    )
                }
            }
        ) {
            Text("Request")
        }
    }
}
