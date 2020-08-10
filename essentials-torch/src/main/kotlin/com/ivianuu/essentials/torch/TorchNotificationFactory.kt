/*
 * Copyright 2019 Manuel Wrage
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
import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.util.Resources
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.setSmallIcon
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@SuppressLint("NewApi")
@Reader
internal fun createTorchNotification(): Notification {
    val notificationManager = given<NotificationManager>()
    val systemBuildInfo = given<SystemBuildInfo>()
    if (systemBuildInfo.sdk >= 26) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                Resources.getString(R.string.es_notif_channel_torch),
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        .apply {
            setAutoCancel(true)
            setContentTitle(Resources.getString(R.string.es_notif_title_torch))
            setContentText(Resources.getString(R.string.es_notif_text_torch))
            setContentIntent(
                PendingIntent.getBroadcast(
                    applicationContext,
                    1,
                    Intent(TorchManager.ACTION_TOGGLE_TORCH),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        .build()
        .apply {
            setSmallIcon(Icon.createWithBitmap(Icons.Default.FlashOn.toBitmap(applicationContext)))
        }
}

private const val NOTIFICATION_CHANNEL_ID = "torch"
