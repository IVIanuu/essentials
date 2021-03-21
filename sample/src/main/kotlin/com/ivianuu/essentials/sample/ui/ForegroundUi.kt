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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.foreground.ForegroundState
import com.ivianuu.essentials.foreground.ForegroundState.Background
import com.ivianuu.essentials.foreground.ForegroundState.Foreground
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive

@Given
val foregroundHomeItem = HomeItem("Foreground") { ForegroundKey() }

class ForegroundKey : Key<Nothing>

@Given
val foregroundKeyModule = KeyModule<ForegroundKey>()

@SuppressLint("NewApi")
@Given
fun foregroundUi(
    @Given foregroundState: ForegroundScreenState,
    @Given notificationFactory: ForegroundNotificationFactory
): KeyUi<ForegroundKey> = {
    val currentForegroundState by foregroundState.collectAsState()

    DisposableEffect(true) {
        onDispose {
            foregroundState.value = Background
        }
    }

    val primaryColor = MaterialTheme.colors.primary

    Scaffold(
        topBar = { TopAppBar(title = { Text("Foreground") }) }
    ) {
        var count by remember(currentForegroundState is Foreground) { mutableStateOf(0) }

        if (currentForegroundState is Foreground) {
            LaunchedEffect(true) {
                while (isActive) {
                    count++
                    foregroundState.value = Foreground(
                        notificationFactory(count, primaryColor)
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
                    else Foreground(notificationFactory(count, primaryColor)))
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

@Given
fun foregroundScreenState(): @Scoped<AppGivenScope> ForegroundScreenState =
    MutableStateFlow(Background)

typealias ForegroundNotificationFactory = (Int, Color) -> Notification

@SuppressLint("NewApi")
@Given
fun foregroundNotificationFactory(
    @Given appContext: AppContext,
    @Given notificationManager: NotificationManager,
    @Given systemBuildInfo: SystemBuildInfo
): ForegroundNotificationFactory = { count, color ->
    if (systemBuildInfo.sdk >= 26) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "foreground", "Foreground",
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }
    NotificationCompat.Builder(appContext, "foreground")
        .setSmallIcon(R.drawable.ic_home)
        .setContentTitle("Foreground")
        .setContentText("Current progress $count")
        .setColor(color.toArgb())
        .build()
}