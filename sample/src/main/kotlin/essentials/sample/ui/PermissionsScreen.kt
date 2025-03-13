/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastMap
import essentials.accessibility.*
import essentials.compose.*
import essentials.notificationlistener.*
import essentials.permission.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsScreen() }

class PermissionsScreen : Screen<Unit>

@Provide @Composable fun PermissionsUi(
  permissionManager: PermissionManager,
  permissions: List<SamplePermission>
): Ui<PermissionsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Permissions") } }) {
    Button(
      modifier = Modifier.fillMaxSize().wrapContentSize(),
      onClick = scopedAction {
        permissionManager.ensurePermissions(permissions.fastMap { it::class })
      }
    ) {
      Text("Request")
    }
  }
}

interface SamplePermission : Permission

@Provide class BluetoothPermission : RuntimePermission(
  permissionName = android.Manifest.permission.BLUETOOTH,
  title = "Bluetooth",
  icon = { Icon(Icons.Default.Bluetooth, null) }
), SamplePermission

@Provide class SampleCameraPermission : RuntimePermission(
  permissionName = android.Manifest.permission.CAMERA,
  title = "Camera",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Camera, null) }
), SamplePermission

@Provide class SamplePhonePermission : RuntimePermission(
  permissionName = android.Manifest.permission.CALL_PHONE,
  title = "Call phone",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Phone, null) }
), SamplePermission

@Provide class SampleAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Accessibility, null) }
), SamplePermission

@Provide class SampleNotificationListenerPermission : NotificationListenerPermission(
  serviceClass = EsNotificationListenerService::class,
  title = "Notification listener",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Notifications, null) }
), SamplePermission

@Provide class SampleSystemOverlayPermission : SystemOverlayPermission(
  title = "System overlay",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Window, null) }
), SamplePermission

@Provide class SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission(
  title = "Write secure settings",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.PowerSettingsNew, null) }
), SamplePermission

@Provide class SampleWriteSettingsPermission : WriteSettingsPermission(
  title = "Write settings",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Settings, null) }
), SamplePermission
