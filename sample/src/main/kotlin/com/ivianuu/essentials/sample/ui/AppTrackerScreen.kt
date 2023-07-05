/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.Notification
import com.ivianuu.essentials.util.NotificationModel
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Provide val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerScreen() }

class AppTrackerScreen : Screen<Unit>

@Provide fun appTrackerUi(
  foregroundManager: ForegroundManager,
  permissionManager: PermissionManager,
  scope: ScopedCoroutineScope<ScreenScope>
) = Ui<AppTrackerScreen, Unit> {
  var isEnabled by remember { mutableStateOf(false) }

  if (isEnabled)
    LaunchedEffect(true) {
      foregroundManager.startForeground(AppTrackerNotification)
    }

  Scaffold(
    topBar = { TopAppBar(title = { Text("App tracker") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
          if (permissionManager.requestPermissions(listOf(typeKeyOf<SampleAccessibilityPermission>())))
            isEnabled = !isEnabled
        }
      }
    ) {
      Text("Toggle tracking")
    }
  }
}

@Serializable object AppTrackerNotification : Notification("app_tracker", "App tracking", Importance.LOW)

@Provide fun appTrackNotificationModel(
  currentApp: Flow<CurrentApp?>,
  resources: Resources
) = Model {
  NotificationModel<AppTrackerNotification>(
    icon = resources(R.mipmap.ic_launcher),
    title = "Current app: ${currentApp.collectAsState(null).value?.value}"
  )
}

@Provide object AppTrackerAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "Needs the permission to track the current app"
)
