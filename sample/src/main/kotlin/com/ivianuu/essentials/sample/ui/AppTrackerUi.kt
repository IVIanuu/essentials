/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.accessibilityservice.*
import android.app.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.recentapps.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

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
