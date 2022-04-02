/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationManager
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Provide val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey }

object AppTrackerKey : Key<Unit>

@Provide fun appTrackerUi(
  currentApp: Flow<CurrentApp?>,
  foregroundManager: ForegroundManager,
  permissionRequester: PermissionRequester,
  scope: NamedCoroutineScope<KeyUiScope>,
  N: NotificationFactory,
  T: ToastContext
) = SimpleKeyUi<AppTrackerKey> {
  var isEnabled by remember { mutableStateOf(false) }

  if (isEnabled)
    LaunchedEffect(true) {
      foregroundManager.startForeground(
        24,
        currentApp
          .map { AppTrackerNotification(it) }
          .stateIn(scope, SharingStarted.Eagerly, AppTrackerNotification(null))
      )
    }

  Scaffold(
    topBar = { TopAppBar(title = { Text("App tracker") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
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
  override val title: String
    get() = "Accessibility"
  override val desc: String
    get() = "Needs the permission to track the current app"
}
