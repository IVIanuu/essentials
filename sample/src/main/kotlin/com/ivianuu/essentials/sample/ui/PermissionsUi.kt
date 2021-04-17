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

import android.accessibilityservice.*
import android.service.notification.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.notificationlistener.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.permission.notificationlistener.*
import com.ivianuu.essentials.permission.runtime.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlin.reflect.*

@Given
val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey }

object PermissionsKey : Key<Nothing>

@Given
fun permissionUi(
    @Given permissionRequester: PermissionRequester,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>
): KeyUi<PermissionsKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Permissions") }) }
    ) {
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    permissionRequester(
                        listOf(
                            typeKeyOf<SampleCameraPermission>(),
                            typeKeyOf<SamplePhonePermission>(),
                            typeKeyOf<SampleAccessibilityPermission>(),
                            typeKeyOf<SampleNotificationListenerPermission>(),
                            typeKeyOf<SampleSystemOverlayPermission>(),
                            typeKeyOf<SampleWriteSecureSettingsPermission>(),
                            typeKeyOf<SampleWriteSettingsPermission>()
                        )
                    )
                }
            }
        ) {
            Text("Request")
        }
    }
}

@Given
object SampleCameraPermission : RuntimePermission {
    override val permissionName: String
        get() = android.Manifest.permission.CAMERA
    override val title: String = "Camera"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Given
object SamplePhonePermission : RuntimePermission {
    override val permissionName: String
        get() = android.Manifest.permission.CALL_PHONE
    override val title: String = "Call phone"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Given
object SampleAccessibilityPermission : AccessibilityServicePermission {
    override val serviceClass: KClass<out AccessibilityService>
        get() = EsAccessibilityService::class
    override val title: String = "Accessibility"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Given
object SampleNotificationListenerPermission : NotificationListenerPermission {
    override val serviceClass: KClass<out NotificationListenerService>
        get() = EsNotificationListenerService::class
    override val title: String = "Notification listener"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Given
object SampleSystemOverlayPermission : SystemOverlayPermission {
    override val title: String = "System overlay"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Given
object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission {
    override val title: String = "Write secure settings"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Given
object SampleWriteSettingsPermission : WriteSettingsPermission {
    override val title: String = "Write settings"
    override val desc: String = "This is a desc"
    override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}
