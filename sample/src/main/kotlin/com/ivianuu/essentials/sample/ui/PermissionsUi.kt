/*
 * Copyright 2021 Manuel Wrage
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

import android.accessibilityservice.AccessibilityService
import android.app.admin.DeviceAdminReceiver
import android.service.notification.NotificationListenerService
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.deviceadmin.DeviceAdminPermission
import com.ivianuu.essentials.permission.ignorebatteryoptimizations.IgnoreBatteryOptimizationsPermission
import com.ivianuu.essentials.permission.installunknownapps.InstallUnknownAppsPermission
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.packageusagestats.PackageUsageStatsPermission
import com.ivianuu.essentials.permission.runtime.RuntimePermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.sample.deviceadmin.SampleDeviceAdmin
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlin.reflect.KClass

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey }

object PermissionsKey : Key<Unit>

@Provide fun permissionUi(permissionRequester: PermissionRequester): KeyUi<PermissionsKey> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Permissions") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        launch {
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
  override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu) }
}

@Provide object SamplePhonePermission : RuntimePermission {
  override val permissionName: String
    get() = android.Manifest.permission.CALL_PHONE
  override val title: String = "Call phone"
  override val desc: String = "This is a desc"
  override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu) }
}

@Provide object SampleAccessibilityPermission : AccessibilityServicePermission {
  override val serviceClass: KClass<out AccessibilityService>
    get() = EsAccessibilityService::class
  override val title: String = "Accessibility"
  override val desc: String = "This is a desc"
  override val icon: @Composable () -> Unit = { Icon(Icons.Default.Menu) }
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
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SampleIgnoreBatteryOptimizationsPermission : IgnoreBatteryOptimizationsPermission {
  override val title: String
    get() = "Ignore battery optimizations"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SampleInstallUnknownAppsPermission : InstallUnknownAppsPermission {
  override val title: String
    get() = "Install unknown apps"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SamplePackageUsageStatsPermission : PackageUsageStatsPermission {
  override val title: String
    get() = "Package usage stats"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SampleNotificationListenerPermission : NotificationListenerPermission {
  override val serviceClass: KClass<out NotificationListenerService>
    get() = EsNotificationListenerService::class
  override val title: String
    get() = "Notification listener"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SampleSystemOverlayPermission : SystemOverlayPermission {
  override val title: String
    get() = "System overlay"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission {
  override val title: String
    get() = "Write secure settings"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}

@Provide object SampleWriteSettingsPermission : WriteSettingsPermission {
  override val title: String
    get() = "Write settings"
  override val desc: String
    get() = "This is a desc"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Menu) }
}
