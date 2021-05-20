/*
 * Copyright 2020 Manuel Wrage
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

import android.accessibilityservice.*
import android.annotation.*
import android.app.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.core.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.recentapps.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Provide val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey }

object AppTrackerKey : Key<Nothing>

@Provide fun appTrackerUi(
  currentApp: Flow<CurrentApp>,
  foregroundState: AppTrackerForegroundState,
  createNotification: (@Provide CurrentApp) -> AppTrackerNotification,
  permissionRequester: PermissionRequester,
  scope: InjectCoroutineScope<KeyUiScope>,
  toaster: Toaster,
): KeyUi<AppTrackerKey> = {
  val currentForegroundState by foregroundState.collectAsState()

  if (currentForegroundState is Foreground) {
    LaunchedEffect(true) {
      runWithCleanup(
        block = {
          currentApp.collect {
            toaster("App changed $it")
            foregroundState.value = Foreground(createNotification(it))
          }
        },
        cleanup = {
          foregroundState.value = Background
        }
      )
    }
  }

  Scaffold(
    topBar = { TopAppBar(title = { Text("App tracker") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
          if (permissionRequester(listOf(typeKeyOf<SampleAccessibilityPermission>()))) {
            foregroundState.value = if (currentForegroundState is Foreground) Background
            else Foreground(createNotification(null))
          }
        }
      }
    ) {
      Text("Toggle tracking")
    }
  }
}

typealias AppTrackerForegroundState = MutableStateFlow<ForegroundState>

@Provide val appTrackerForegroundState: @Scoped<AppScope> AppTrackerForegroundState
  get() = MutableStateFlow(Background)

typealias AppTrackerNotification = Notification

@SuppressLint("NewApi")
@Provide
fun appTrackerNotification(
  context: AppContext,
  currentApp: CurrentApp,
  notificationManager: @SystemService NotificationManager,
  systemBuildInfo: SystemBuildInfo
): AppTrackerNotification {
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
