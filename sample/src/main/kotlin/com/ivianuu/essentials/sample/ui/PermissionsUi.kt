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
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.launch

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey() }

class PermissionsKey : Key<Unit>

@Provide fun permissionUi(
  ctx: KeyUiContext<PermissionsKey>,
  permissionManager: PermissionManager
) = KeyUi<PermissionsKey, Unit> { model ->
  Scaffold(
    topBar = { TopAppBar(title = { Text("Permissions") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        ctx.launch {
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
      }
    ) {
      Text("Request")
    }
  }
}

@Provide object SampleCameraPermission : RuntimePermission(
  permissionName = android.Manifest.permission.CAMERA,
  title = "Camera",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)

@Provide object SamplePhonePermission : RuntimePermission(
  permissionName = android.Manifest.permission.CALL_PHONE,
  title = "Call phone",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)

@Provide object SampleAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)

@Provide object SampleNotificationListenerPermission : NotificationListenerPermission(
  serviceClass = EsNotificationListenerService::class,
  title = "Notification listener",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)

@Provide object SampleSystemOverlayPermission : SystemOverlayPermission(
  title = "System overlay",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)

@Provide object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission(
  title = "Write secure settings",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)

@Provide object SampleWriteSettingsPermission : WriteSettingsPermission(
  title = "Write settings",
  desc = "This is a desc",
  icon = Permission.Icon { Icon(Icons.Default.Menu) }
)
