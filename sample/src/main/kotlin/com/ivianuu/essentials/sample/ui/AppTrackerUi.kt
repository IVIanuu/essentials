/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationManager
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

@Provide val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey }

object AppTrackerKey : Key<Unit>

@Provide fun appTrackerUi(
  currentApp: Flow<CurrentApp?>,
  foregroundManager: ForegroundManager,
  permissionRequester: PermissionRequester,
  N: NotificationFactory,
  S: NamedCoroutineScope<KeyUiScope>,
  T: ToastContext
) = KeyUi<AppTrackerKey> {
  var isEnabled by remember { mutableStateOf(false) }

  if (isEnabled)
    LaunchedEffect(true) {
      foregroundManager.startForeground(24) {
        val currentApp = valueFromFlow(null) { currentApp }
        LaunchedEffect(currentApp) {
          showToast("App changed $currentApp")
        }
        AppTrackerNotification(currentApp)
      }
    }

  Scaffold(
    topBar = { TopAppBar(title = { Text("App tracker") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        launch {
          if (permissionRequester(listOf(typeKeyOf<SampleAccessibilityPermission>()))) {
            isEnabled = !isEnabled
          }
        }
      }
    ) {
      Text("Toggle tracking")
    }
  }
}

private fun AppTrackerNotification(
  currentApp: CurrentApp?,
  @Inject factory: NotificationFactory
): Notification =
  factory.build("app_tracker", "App tracking", NotificationManager.IMPORTANCE_LOW) {
    setSmallIcon(R.mipmap.ic_launcher)
    setContentTitle("Current app: ${currentApp?.value}")
  }

@Provide object AppTrackerAccessibilityPermission : AccessibilityServicePermission {
  override val serviceClass: KClass<out AccessibilityService>
    get() = EsAccessibilityService::class
  override val title: String = "Accessibility"
  override val desc: String = "Needs the permission to track the current app"
  @Composable override fun Icon() {
  }
}
