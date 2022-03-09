/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.accessibilityservice.*
import android.service.notification.*
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
import com.ivianuu.essentials.permission.notificationlistener.*
import com.ivianuu.essentials.permission.runtime.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.*
import kotlin.reflect.*

@Provide val permissionsHomeItem: HomeItem = HomeItem("Permissions") { PermissionsKey }

object PermissionsKey : Key<Unit>

@Provide fun permissionUi(
  permissionRequester: PermissionRequester,
  scope: NamedCoroutineScope<KeyUiScope>
) = KeyUi<PermissionsKey> {
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

@Provide object SampleCameraPermission : RuntimePermission {
  override val permissionName: String
    get() = android.Manifest.permission.CAMERA
  override val title: String = "Camera"
  override val desc: String = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}

@Provide object SamplePhonePermission : RuntimePermission {
  override val permissionName: String
    get() = android.Manifest.permission.CALL_PHONE
  override val title: String = "Call phone"
  override val desc: String = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}

@Provide object SampleAccessibilityPermission : AccessibilityServicePermission {
  override val serviceClass: KClass<out AccessibilityService>
    get() = EsAccessibilityService::class
  override val title: String = "Accessibility"
  override val desc: String = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}

@Provide object SampleNotificationListenerPermission : NotificationListenerPermission {
  override val serviceClass: KClass<out NotificationListenerService>
    get() = EsNotificationListenerService::class
  override val title: String
    get() = "Notification listener"
  override val desc: String
    get() = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}

@Provide object SampleSystemOverlayPermission : SystemOverlayPermission {
  override val title: String
    get() = "System overlay"
  override val desc: String
    get() = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}

@Provide object SampleWriteSecureSettingsPermission : WriteSecureSettingsPermission {
  override val title: String
    get() = "Write secure settings"
  override val desc: String
    get() = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}

@Provide object SampleWriteSettingsPermission : WriteSettingsPermission {
  override val title: String
    get() = "Write settings"
  override val desc: String
    get() = "This is a desc"
  @Composable override fun Icon() {
    Icon(Icons.Default.Menu)
  }
}
