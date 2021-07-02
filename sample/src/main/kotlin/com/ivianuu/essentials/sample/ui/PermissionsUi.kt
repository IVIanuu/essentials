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
import android.app.admin.*
import android.service.notification.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.notificationlistener.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.permission.deviceadmin.*
import com.ivianuu.essentials.permission.ignorebatteryoptimizations.*
import com.ivianuu.essentials.permission.installunknownapps.*
import com.ivianuu.essentials.permission.notificationlistener.*
import com.ivianuu.essentials.permission.packageusagestats.*
import com.ivianuu.essentials.permission.runtime.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.essentials.sample.deviceadmin.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlin.reflect.*

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey }

object PermissionsKey : Key<Nothing>

@Provide fun permissionUi(
  permissionRequester: PermissionRequester,
  scope: InjektCoroutineScope<KeyUiScope>
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
              typeKeyOf<SampleDeviceAdminPermission>(),
              typeKeyOf<SamplePhonePermission>(),
              typeKeyOf<SampleAccessibilityPermission>(),
              typeKeyOf<SampleNotificationListenerPermission>(),
              typeKeyOf<SampleIgnoreBatteryOptimizationsPermission>(),
              typeKeyOf<SampleInstallUnknownAppsPermission>(),
              typeKeyOf<SamplePackageUsageStatsPermission>(),
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

@Provide object SampleCameraPermission : RuntimePermission {
  override val permissionName: String
    get() = android.Manifest.permission.CAMERA
  override val title: String = "Camera"
  override val desc: String = "This is a desc"
  override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Provide object SamplePhonePermission : RuntimePermission {
  override val permissionName: String
    get() = android.Manifest.permission.CALL_PHONE
  override val title: String = "Call phone"
  override val desc: String = "This is a desc"
  override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleAccessibilityPermission : AccessibilityServicePermission {
  override val serviceClass: KClass<out AccessibilityService>
    get() = EsAccessibilityService::class
  override val title: String = "Accessibility"
  override val desc: String = "This is a desc"
  override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleDeviceAdminPermission : DeviceAdminPermission {
  override val deviceAdminClass: KClass<out DeviceAdminReceiver>
    get() = SampleDeviceAdmin::class
  override val explanation: String
    get() = "Explanation"
  override val title: String
    get() = "Device admin"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleIgnoreBatteryOptimizationsPermission : IgnoreBatteryOptimizationsPermission {
  override val title: String
    get() = "Ignore battery optimizations"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleInstallUnknownAppsPermission : InstallUnknownAppsPermission {
  override val title: String
    get() = "Install unknown apps"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SamplePackageUsageStatsPermission : PackageUsageStatsPermission {
  override val title: String
    get() = "Package usage stats"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleNotificationListenerPermission : NotificationListenerPermission {
  override val serviceClass: KClass<out NotificationListenerService>
    get() = EsNotificationListenerService::class
  override val title: String
    get() = "Notification listener"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleSystemOverlayPermission : SystemOverlayPermission {
  override val title: String
    get() = "System overlay"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission {
  override val title: String
    get() = "Write secure settings"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}

@Provide object SampleWriteSettingsPermission : WriteSettingsPermission {
  override val title: String
    get() = "Write settings"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu, null) }
}
