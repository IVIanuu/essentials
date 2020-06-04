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

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.FlashOn
import com.ivianuu.essentials.foreground.AbstractNotificationFactory
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.setSmallIcon
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

@Transient
internal class TorchNotificationFactory(
    context: @ForApplication Context,
    private val resourceProvider: ResourceProvider
) : AbstractNotificationFactory(context) {

    @TargetApi(26)
    override fun createChannel() = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        resourceProvider.getString(R.string.es_notif_channel_torch),
        NotificationManager.IMPORTANCE_LOW
    )

    override fun buildNotification(context: Context): Notification =
        NotificationCompat.Builder(context,
            NOTIFICATION_CHANNEL_ID
        )
            .apply {
                setAutoCancel(true)
                setContentTitle(resourceProvider.getString(R.string.es_notif_title_torch))
                setContentText(resourceProvider.getString(R.string.es_notif_text_torch))
                setContentIntent(
                    PendingIntent.getBroadcast(
                        context,
                        1,
                        Intent(TorchManager.ACTION_TOGGLE_TORCH),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            }
            .build()
            .apply {
                setSmallIcon(Icon.createWithBitmap(Icons.Default.FlashOn.toBitmap(context)))
            }

    private companion object {
        private const val NOTIFICATION_CHANNEL_ID = "torch"
    }
}
