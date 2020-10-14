package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.setValue
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
import com.ivianuu.injekt.Assisted
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

@FunBinding
internal fun buildForegroundNotification(
    applicationContext: ApplicationContext,
    count: @Assisted Int,
    color: @Assisted Color,
): Notification = NotificationCompat.Builder(
    applicationContext,
    "foreground"
)
    .setSmallIcon(R.drawable.ic_home)
    .setContentTitle("Foreground")
    .setContentText("Current progress $count")
    .setColor(color.toArgb())
    .build()
