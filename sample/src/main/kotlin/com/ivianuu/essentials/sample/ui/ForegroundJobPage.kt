package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.launchInComposition
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.setValue
import androidx.compose.state
import androidx.compose.stateFor
import androidx.core.app.NotificationCompat
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.foreground.ForegroundJob
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.delay

@Transient
class ForegroundJobPage(
    private val context: @ForApplication Context,
    private val foregroundManager: ForegroundManager,
    private val notificationManager: NotificationManager,
    private val systemBuildInfo: SystemBuildInfo
) {

    @SuppressLint("NewApi")
    @Composable
    operator fun invoke() {
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
            topAppBar = { TopAppBar(title = { Text("Foreground") }) },
            body = {
                var foregroundJob by state<ForegroundJob?> { null }
                var count by stateFor(foregroundJob) { 0 }

                foregroundJob?.let { currentJob ->
                    onCommit(count) {
                        currentJob.updateNotification(
                            buildNotification(count, primaryColor)
                        )
                    }

                    launchInComposition(currentJob) {
                        while (true) {
                            delay(1000)
                            count++
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalGravity = Alignment.CenterHorizontally
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
                            if (foregroundJob != null) {
                                foregroundJob?.stop()
                                foregroundJob = null
                            } else {
                                foregroundJob = foregroundManager.startJob(
                                    buildNotification(count, primaryColor)
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
        )
    }

    private fun buildNotification(
        count: Int,
        color: Color
    ) = NotificationCompat.Builder(
        context,
        "foreground"
    )
        .setContentTitle("Foreground")
        .setContentText("Current progress $count")
        .setColor(color.toArgb())
        .build()
}
