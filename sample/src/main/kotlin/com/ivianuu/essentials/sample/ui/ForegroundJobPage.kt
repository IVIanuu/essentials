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
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.foreground.ForegroundJob
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.delay

@SuppressLint("NewApi")
@FunBinding
@Composable
fun ForegroundJobPage(
    buildForegroundNotification: buildForegroundNotification,
    foregroundManager: ForegroundManager,
    notificationManager: NotificationManager,
    systemBuildInfo: com.ivianuu.essentials.util.SystemBuildInfo,
) {
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

    val primaryColor = MaterialTheme.colors.primary

    Scaffold(
        topBar = { TopAppBar(title = { Text("Foreground") }) }
    ) {
        var foregroundJob by rememberState<ForegroundJob?> { null }
        var count by rememberState(foregroundJob) { 0 }

        foregroundJob?.let { currentJob ->
            onCommit(count) {
                currentJob.updateNotification(
                    buildForegroundNotification(count, primaryColor)
                )
            }

            LaunchedTask(currentJob) {
                while (true) {
                    delay(1000)
                    count++
                }
            }

            onDispose { foregroundJob?.stop() }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (foregroundJob != null) {
                Text(
                    text = "Current progress $count",
                    style = MaterialTheme.typography.h5
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    foregroundJob = if (foregroundJob != null) {
                        foregroundJob?.stop()
                        null
                    } else {
                        foregroundManager.startJob(
                            buildForegroundNotification(count, primaryColor)
                        )
                    }
                }
            ) {
                Text(
                    if (foregroundJob != null) {
                        "Stop foreground"
                    } else {
                        "Start foreground"
                    }
                )
            }
        }
    }
}

typealias buildForegroundNotification = (Int, Color) -> Notification

@Binding
internal fun buildForegroundNotification(
    applicationContext: ApplicationContext,
): buildForegroundNotification = { count, color ->
    NotificationCompat.Builder(applicationContext, "foreground")
        .setSmallIcon(R.drawable.ic_home)
        .setContentTitle("Foreground")
        .setContentText("Current progress $count")
        .setColor(color.toArgb())
        .build()
}
