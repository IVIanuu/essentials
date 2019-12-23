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

import android.Manifest
import androidx.compose.ambient
import androidx.ui.core.ContextAmbient
import androidx.ui.layout.Center
import androidx.ui.material.Button
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.accessibility.ComponentAccessibilityService
import com.ivianuu.essentials.notificationlistener.ComponentNotificationListenerService
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.MetadataKeys
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.runtime.RuntimePermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.coroutines.coroutineScope
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.resources.drawableResource
import kotlinx.coroutines.launch

val PermissionRoute = Route {
    SimpleScreen(title = "Permissions") {
        val manager = inject<PermissionManager>()

        val camera = RuntimePermission(
            Manifest.permission.CAMERA,
            MetadataKeys.Title to "Camera",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val phone = RuntimePermission(
            Manifest.permission.CALL_PHONE,
            MetadataKeys.Title to "Call phone",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val accessibility = AccessibilityServicePermission(
            ComponentAccessibilityService::class,
            MetadataKeys.Title to "Accessibility",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val notificationListener = NotificationListenerPermission(
            ComponentNotificationListenerService::class,
            MetadataKeys.Title to "Notification listener",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val systemOverlay = SystemOverlayPermission(
            ambient(ContextAmbient),
            MetadataKeys.Title to "System overlay",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val writeSettings = WriteSettingsPermission(
            ambient(ContextAmbient),
            MetadataKeys.Title to "Write settings",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val coroutineScope = coroutineScope

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
                            writeSettings
                        )
                        d { "granted $granted" }
                    }
                }
            )
        }
    }
}