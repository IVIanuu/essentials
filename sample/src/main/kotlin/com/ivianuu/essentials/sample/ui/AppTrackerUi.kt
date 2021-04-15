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

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.foreground.ForegroundState
import com.ivianuu.essentials.foreground.ForegroundState.Background
import com.ivianuu.essentials.foreground.ForegroundState.Foreground
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Given
val appTrackerHomeItem = HomeItem("App tracker") { AppTrackerKey() }

class AppTrackerKey : Key<Nothing>

@Given
fun appTrackerUi(
    @Given currentApp: Flow<CurrentApp>,
    @Given foregroundState: AppTrackerForegroundState,
    @Given createNotification: (@Given CurrentApp) -> AppTrackerNotification,
    @Given permissionRequester: PermissionRequester,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given toaster: Toaster,
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

@Given
val appTrackerForegroundState: @Scoped<AppGivenScope> AppTrackerForegroundState
    get() = MutableStateFlow(Background)

typealias AppTrackerNotification = Notification

@SuppressLint("NewApi")
@Given
fun appTrackerNotification(
    @Given appContext: AppContext,
    @Given currentApp: CurrentApp,
    @Given notificationManager: @SystemService NotificationManager,
    @Given systemBuildInfo: SystemBuildInfo
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
    return NotificationCompat.Builder(appContext, "app_tracker")
        .apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Current app: $currentApp")
        }
        .build()
}

@Given
object AppTrackerAccessibilityPermission : AccessibilityServicePermission {
    override val serviceClass: KClass<out AccessibilityService>
        get() = EsAccessibilityService::class
    override val title: String = "Accessibility"
    override val desc: String = "Needs the permission to track the current app"
    override val icon: @Composable (() -> Unit)?
        get() = null
}
