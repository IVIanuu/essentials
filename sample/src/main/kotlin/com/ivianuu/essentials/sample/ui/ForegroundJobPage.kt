package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.launchInComposition
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.onDispose
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
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ChatBubble
import androidx.ui.unit.dp
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.foreground.ForegroundJob
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.setSmallIcon
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.get
import kotlinx.coroutines.delay

@SuppressLint("NewApi")
@Reader
@Composable
fun ForegroundJobPage() {
    if (get<SystemBuildInfo>().sdk >= 26) {
        onActive {
            get<NotificationManager>().createNotificationChannel(
                NotificationChannel(
                    "foreground", "Foreground",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    val primaryColor = MaterialTheme.colors.primary

    val foregroundManager = get<ForegroundManager>()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Foreground") }) }
    ) {
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

            onDispose { foregroundJob?.stop() }
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
                    foregroundJob = if (foregroundJob != null) {
                        foregroundJob?.stop()
                        null
                    } else {
                        foregroundManager.startJob(
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
}

@Reader
private fun buildNotification(
    count: Int,
    color: Color
) = NotificationCompat.Builder(
    applicationContext,
    "foreground"
)
    .setContentTitle("Foreground")
    .setContentText("Current progress $count")
    .setColor(color.toArgb())
    .build()
    .apply {
        setSmallIcon(Icon.createWithBitmap(Icons.Default.ChatBubble.toBitmap(applicationContext)))
    }
