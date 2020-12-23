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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.foreground.ForegroundState
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.foreground.ForegroundStateBinding
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive

@HomeItemBinding("Foreground")
class ForegroundKey

@SuppressLint("NewApi")
@KeyUiBinding<ForegroundKey>
@GivenFun
@Composable
fun ForegroundScreen(
    createForegroundNotification: createForegroundNotification,
    foregroundState: ForegroundScreenState,
    notificationManager: NotificationManager,
    systemBuildInfo: SystemBuildInfo,
) {
    val currentForegroundState by foregroundState.collectAsState()
    if (systemBuildInfo.sdk >= 26) {
        onActive {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "foreground", "Foreground",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    onDispose { foregroundState.value = Background }

    val primaryColor = MaterialTheme.colors.primary

    Scaffold(
        topBar = { TopAppBar(title = { Text("Foreground") }) }
    ) {
        var count by rememberState(currentForegroundState is Foreground) { 0 }

        if (currentForegroundState is Foreground) {
            LaunchedEffect(true) {
                while (isActive) {
                    count++
                    foregroundState.value = Foreground(
                        createForegroundNotification(count, primaryColor)
                    )
                    delay(1000)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentForegroundState is Foreground) {
                Text(
                    text = "Current progress $count",
                    style = MaterialTheme.typography.h5
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    foregroundState.value = (if (currentForegroundState is Foreground) Background
                    else Foreground(createForegroundNotification(count, primaryColor)))
                }
            ) {
                Text(
                    if (currentForegroundState is Foreground) {
                        "Stop foreground"
                    } else {
                        "Start foreground"
                    }
                )
            }
        }
    }
}

typealias ForegroundScreenState = MutableStateFlow<ForegroundState>

@Scoped(ApplicationComponent::class)
@Given
fun foregroundScreenState(): ForegroundScreenState = MutableStateFlow(Background)

// todo remove once injekt fixes effect scoping issues
@ForegroundStateBinding
inline val ForegroundScreenState.bindForegroundScreenState: ForegroundScreenState
    get() = this

@GivenFun
fun createForegroundNotification(
    applicationContext: ApplicationContext,
    @FunApi count: Int,
    @FunApi color: Color,
): Notification = NotificationCompat.Builder(applicationContext, "foreground")
    .setSmallIcon(R.drawable.ic_home)
    .setContentTitle("Foreground")
    .setContentText("Current progress $count")
    .setColor(color.toArgb())
    .build()
