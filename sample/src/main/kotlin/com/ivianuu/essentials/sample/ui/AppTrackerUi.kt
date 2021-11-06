/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemBuildInfo
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
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Provide val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey }

object AppTrackerKey : Key<Unit>

@Provide fun appTrackerUi(
  currentApp: Flow<CurrentApp?>,
  createNotification: (CurrentApp?) -> @AppTracker Notification,
  foregroundManager: ForegroundManager,
  permissionRequester: PermissionRequester,
  scope: ComponentScope<KeyUiComponent>,
  toaster: Toaster,
): KeyUi<AppTrackerKey> = {
  var isEnabled by remember { mutableStateOf(false) }

  if (isEnabled)
    LaunchedEffect(true) {
      foregroundManager.startForeground(
        24,
        currentApp
          .onEach { showToast("App changed $it") }
          .map { createNotification(it) }
          .stateIn(this, SharingStarted.Eagerly, createNotification(null))
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

@Tag private annotation class AppTracker

@SuppressLint("NewApi")
@Provide
fun appTrackerNotification(
  context: AppContext,
  currentApp: CurrentApp?,
  notificationManager: @SystemService NotificationManager,
  systemBuildInfo: SystemBuildInfo
): @AppTracker Notification {
  if (systemBuildInfo.sdk >= 26) {
    notificationManager.createNotificationChannel(
      NotificationChannel(
        "app_tracker",
        "App tracking",
        NotificationManager.IMPORTANCE_LOW
      )
    )
  }
  return NotificationCompat.Builder(context, "app_tracker")
    .apply {
      setSmallIcon(R.mipmap.ic_launcher)
      setContentTitle("Current app: $currentApp")
    }
    .build()
}

@Provide object AppTrackerAccessibilityPermission : AccessibilityServicePermission {
  override val serviceClass: KClass<out AccessibilityService>
    get() = EsAccessibilityService::class
  override val title: String = "Accessibility"
  override val desc: String = "Needs the permission to track the current app"
  override val icon: @Composable (() -> Unit)?
    get() = null
}
