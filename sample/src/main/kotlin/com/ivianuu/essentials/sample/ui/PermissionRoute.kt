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
import androidx.ui.core.ContextAmbient
import androidx.ui.layout.Center
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.accessibility.ComponentAccessibilityService
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.Menu
import com.ivianuu.essentials.notificationlistener.ComponentNotificationListenerService
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.runtime.RuntimePermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.coroutines.CoroutineScopeAmbient
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.painter.VectorRenderable
import kotlinx.coroutines.launch

val PermissionRoute = Route {
    SimpleScreen(title = "Permissions") {
        val manager = inject<PermissionManager>()

        val camera = RuntimePermission(
            Manifest.permission.CAMERA,
            Metadata.Title withValue "Camera",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val phone = RuntimePermission(
            Manifest.permission.CALL_PHONE,
            Metadata.Title withValue "Call phone",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val accessibility = AccessibilityServicePermission(
            ComponentAccessibilityService::class,
            Metadata.Title withValue "Accessibility",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val notificationListener = NotificationListenerPermission(
            ComponentNotificationListenerService::class,
            Metadata.Title withValue "Notification listener",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val systemOverlay = SystemOverlayPermission(
            ContextAmbient.current,
            Metadata.Title withValue "System overlay",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val writeSecureSettings = WriteSecureSettingsPermission(
            Metadata.Title withValue "Write secure settings",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val writeSettings = WriteSettingsPermission(
            ContextAmbient.current,
            Metadata.Title withValue "Write settings",
            Metadata.Desc withValue "This is a desc",
            Metadata.Icon withValue VectorRenderable(Icons.Default.Menu)
        )

        val coroutineScope = CoroutineScopeAmbient.current

        Center {
            Button(
                text = "Request",
                onClick = {
                    coroutineScope.launch {
                        val granted = manager.request(
                            camera,
                            phone,
                            accessibility,
                            notificationListener,
                            systemOverlay,
                            writeSecureSettings,
                            writeSettings
                        )
                        d { "granted $granted" }
                    }
                }
            )
        }
    }
}