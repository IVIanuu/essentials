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

package com.ivianuu.essentials.torch

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.app.AppWorkerBinding
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.coroutines.applyState
import com.ivianuu.essentials.foreground.ForegroundJob
import com.ivianuu.essentials.foreground.startForegroundJob
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AppWorkerBinding
@FunBinding
suspend fun updateTorchForegroundState(
    createTorchNotification: createTorchNotification,
    startForegroundJob: startForegroundJob,
    state: Flow<TorchState>
) {
    state
        .map { it.torchEnabled }
        .distinctUntilChanged()
        .onCancel { emit(false) }
        .applyState(null as ForegroundJob?) { torchEnabled ->
            if (torchEnabled) {
                startForegroundJob(createTorchNotification())
            } else {
                this?.stop()
                null
            }
        }
        .collect()
}

@SuppressLint("NewApi")
@FunBinding
fun createTorchNotification(
    applicationContext: ApplicationContext,
    notificationManager: NotificationManager,
    stringResource: stringResource,
    systemBuildInfo: SystemBuildInfo,
): Notification {
    if (systemBuildInfo.sdk >= 26) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                stringResource(R.string.es_notif_channel_torch),
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        .apply {
            setAutoCancel(true)
            setSmallIcon(R.drawable.es_ic_flash_on)
            setContentTitle(stringResource(R.string.es_notif_title_torch))
            setContentText(stringResource(R.string.es_notif_text_torch))
            setContentIntent(
                PendingIntent.getBroadcast(
                    applicationContext,
                    1,
                    Intent(ACTION_TOGGLE_TORCH),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        .build()
}

private const val NOTIFICATION_CHANNEL_ID = "torch"
