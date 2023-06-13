/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.app.NotificationManager
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Provide val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey() }

class AppTrackerKey : Key<Unit>

@Provide fun appTrackerUi(
  currentApp: Flow<CurrentApp?>,
  foregroundManager: ForegroundManager,
  navigator: Navigator,
  notificationFactory: NotificationFactory,
  permissionManager: PermissionManager,
  resources: Resources,
  toaster: Toaster
) = Ui<AppTrackerKey, Unit> { model ->
  var isEnabled by remember { mutableStateOf(false) }

  if (isEnabled)
    LaunchedEffect(true) {
      val notifications = currentApp
        .map { AppTrackerNotification(it) }
        .stateIn(this, SharingStarted.Eagerly, AppTrackerNotification(null))
      foregroundManager.runInForeground(notifications.value) {
        notifications.collect { updateNotification(it) }
      }
    }

  Scaffold(
    topBar = { TopAppBar(title = { Text("App tracker") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = action {
        if (permissionManager.requestPermissions(listOf(typeKeyOf<SampleAccessibilityPermission>())))
          isEnabled = !isEnabled
      }
    ) {
      Text("Toggle tracking")
    }
  }
}

private fun AppTrackerNotification(
  currentApp: CurrentApp?,
  @Inject notificationFactory: NotificationFactory
) = notificationFactory("app_tracker", "App tracking", NotificationManager.IMPORTANCE_LOW) {
  setSmallIcon(R.mipmap.ic_launcher)
  setContentTitle("Current app: ${currentApp?.value}")
}

@Provide object AppTrackerAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "Needs the permission to track the current app"
)
