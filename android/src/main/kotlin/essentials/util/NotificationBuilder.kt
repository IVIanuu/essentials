/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
import android.content.*
import androidx.compose.ui.graphics.*
import androidx.core.app.*
import androidx.core.content.*
import essentials.app.*
import essentials.ui.app.*
import injekt.*

inline fun buildNotification(
  channelId: String,
  channelName: String,
  importance: Int,
  context: Context = inject,
  appColors: AppColors = inject,
  builder: NotificationCompat.Builder.() -> Unit
): Notification = buildNotification(
  channel = NotificationChannel(channelId, channelName, importance),
  builder = builder
)

inline fun buildNotification(
  channel: NotificationChannel,
  context: Context = inject,
  appColors: AppColors = inject,
  builder: NotificationCompat.Builder.() -> Unit
): Notification {
  context.getSystemService<NotificationManager>()!!.createNotificationChannel(channel)
  return NotificationCompat.Builder(context, channel.id)
    .defaults()
    .apply(builder)
    .build()
}

@PublishedApi internal fun NotificationCompat.Builder.defaults(
  appColors: AppColors = inject
) = apply {
  color = appColors.primary.toArgb()
}

fun uiLauncherIntent(
  requestCode: Int = 1,
  context: Context = inject,
  intentBuilder: Intent.() -> Unit = {}
): PendingIntent = PendingIntent.getActivity(
  context,
  requestCode,
  Intent(context, EsActivity::class.java).apply(intentBuilder),
  PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
