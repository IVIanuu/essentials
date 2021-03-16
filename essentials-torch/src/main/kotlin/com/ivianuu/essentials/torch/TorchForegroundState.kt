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
import com.ivianuu.essentials.foreground.ForegroundState
import com.ivianuu.essentials.foreground.ForegroundState.Background
import com.ivianuu.essentials.foreground.ForegroundState.Foreground
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Given
fun torchForegroundState(
    @Given state: Flow<TorchState>,
    @Given torchNotificationFactory: TorchNotificationFactory
): Flow<ForegroundState> = state
    .map { it.torchEnabled }
    .distinctUntilChanged()
    .map { torchEnabled ->
        if (torchEnabled) Foreground(torchNotificationFactory())
        else Background
    }

typealias TorchNotificationFactory = () -> Notification

@SuppressLint("NewApi")
@Given
fun torchNotificationFactory(
    @Given appContext: AppContext,
    @Given notificationManager: NotificationManager,
    @Given resourceProvider: ResourceProvider,
    @Given systemBuildInfo: SystemBuildInfo,
): TorchNotificationFactory = {
    if (systemBuildInfo.sdk >= 26) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                resourceProvider.string(R.string.es_notif_channel_torch),
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
        .apply {
            setAutoCancel(true)
            setSmallIcon(R.drawable.es_ic_flash_on)
            setContentTitle(resourceProvider.string(R.string.es_notif_title_torch))
            setContentText(resourceProvider.string(R.string.es_notif_text_torch))
            setContentIntent(
                PendingIntent.getBroadcast(
                    appContext,
                    1,
                    Intent(ACTION_DISABLE_TORCH),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        .build()
}

private const val NOTIFICATION_CHANNEL_ID = "torch"
