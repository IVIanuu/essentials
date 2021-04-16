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

import android.annotation.*
import android.app.*
import android.content.*
import androidx.core.app.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.flow.*

@Given
fun torchForegroundState(
    @Given createTorchNotification: () -> TorchNotification,
    @Given state: Flow<TorchState>
): Flow<ForegroundState> = state
    .map { torchEnabled ->
        if (torchEnabled) Foreground(createTorchNotification())
        else Background
    }

typealias TorchNotification = Notification

@SuppressLint("NewApi")
@Given
fun torchNotification(
    @Given appContext: AppContext,
    @Given notificationManager: @SystemService NotificationManager,
    @Given stringResource: StringResourceProvider,
    @Given systemBuildInfo: SystemBuildInfo,
): TorchNotification {
    if (systemBuildInfo.sdk >= 26) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                stringResource(R.string.es_notif_channel_torch, emptyList()),
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    return NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
        .apply {
            setAutoCancel(true)
            setSmallIcon(R.drawable.es_ic_flash_on)
            setContentTitle(stringResource(R.string.es_notif_title_torch, emptyList()))
            setContentText(stringResource(R.string.es_notif_text_torch, emptyList()))
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
