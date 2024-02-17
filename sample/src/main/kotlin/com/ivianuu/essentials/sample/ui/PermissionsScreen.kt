/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.runtime.RuntimePermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsScreen() }

class PermissionsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      permissionManager: PermissionManager
    ) = Ui<PermissionsScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Permissions") } }) {
        Button(
          modifier = Modifier.center(),
          onClick = action {
            permissionManager.requestPermissions(
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
        ) {
          Text("Request")
        }
      }
    }
  }
}

@Provide object SampleCameraPermission : RuntimePermission(
  permissionName = android.Manifest.permission.CAMERA,
  title = "Camera",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)

@Provide object SamplePhonePermission : RuntimePermission(
  permissionName = android.Manifest.permission.CALL_PHONE,
  title = "Call phone",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleNotificationListenerPermission : NotificationListenerPermission(
  serviceClass = EsNotificationListenerService::class,
  title = "Notification listener",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleSystemOverlayPermission : SystemOverlayPermission(
  title = "System overlay",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission(
  title = "Write secure settings",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleWriteSettingsPermission : WriteSettingsPermission(
  title = "Write settings",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu, null) }
)
