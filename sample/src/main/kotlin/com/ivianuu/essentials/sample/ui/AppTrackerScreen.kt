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

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.foreground.ForegroundState
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.foreground.ForegroundStateBinding
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HomeItemBinding
@Given
val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey() }

class AppTrackerKey

@KeyUiBinding<AppTrackerKey>
@GivenFun
@Composable
fun AppTrackerScreen(
    @Given createAppTrackerNotification: createAppTrackerNotification,
    @Given currentApp: Flow<CurrentApp>,
    @Given foregroundState: AppTrackerForegroundState,
    @Given requestPermissions: requestPermissions,
    @Given showToast: showToast,
) {
    val currentForegroundState by foregroundState.collectAsState()

    if (currentForegroundState is Foreground) {
        LaunchedEffect(true) {
            currentApp
                .onEach {
                    showToast("App changed $it")
                    foregroundState.value = Foreground(createAppTrackerNotification(it))
                }
                .collect()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("App tracker") }) }
    ) {
        val scope = rememberCoroutineScope()
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    if (requestPermissions(
                            listOf(
                                AccessibilityServicePermission(
                                    DefaultAccessibilityService::class,
                                    Permission.Title to "Accessibility",
                                    Permission.Desc to "App tracking requires the accessibility permission"
                                )
                            )
                        )
                    ) {
                        foregroundState.value = if (currentForegroundState is Foreground) Background
                        else Foreground(createAppTrackerNotification(null))
                    }
                }
            }
        ) {
            Text("Toggle tracking")
        }
    }
}

typealias AppTrackerForegroundState = MutableStateFlow<ForegroundState>

@Scoped<AppComponent>
@Given
fun appTrackerForegroundState(): AppTrackerForegroundState = MutableStateFlow(Background)

// todo remove once injekt fixes effect scoping issues
@ForegroundStateBinding
@Given
inline val @Given AppTrackerForegroundState.bindAppTrackerForegroundState: AppTrackerForegroundState
    get() = this

@SuppressLint("NewApi")
@GivenFun
fun createAppTrackerNotification(
    currentApp: String?,
    @Given appContext: AppContext,
    @Given notificationManager: NotificationManager,
    @Given systemBuildInfo: SystemBuildInfo
): Notification {
    if (systemBuildInfo.sdk >= 26) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "app_tracker",
                "App tracking",
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    return NotificationCompat.Builder(appContext, "app_tracker")
        .apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Current app: $currentApp")
        }
        .build()
}