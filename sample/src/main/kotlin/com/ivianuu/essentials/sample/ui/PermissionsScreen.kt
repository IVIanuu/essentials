/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.notificationlistener.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsScreen() }

class PermissionsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      permissionManager: PermissionManager
    ) = Ui<PermissionsScreen> {
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
  icon = { Icon(Icons.Default.Menu, null) }
)

@Provide object SamplePhonePermission : RuntimePermission(
  permissionName = android.Manifest.permission.CALL_PHONE,
  title = "Call phone",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleNotificationListenerPermission : NotificationListenerPermission(
  serviceClass = EsNotificationListenerService::class,
  title = "Notification listener",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleSystemOverlayPermission : SystemOverlayPermission(
  title = "System overlay",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission(
  title = "Write secure settings",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Menu, null) }
)

@Provide object SampleWriteSettingsPermission : WriteSettingsPermission(
  title = "Write settings",
  desc = "This is a desc",
  icon = { Icon(Icons.Default.Menu, null) }
)
