/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
import android.content.*
import androidx.core.app.*
import essentials.*
import essentials.app.*
import injekt.*

inline fun buildNotification(
  channel: NotificationChannel,
  scope: Scope<*> = inject,
  builder: NotificationCompat.Builder.() -> Unit
): Notification {
  systemService<NotificationManager>().createNotificationChannel(channel)
  return NotificationCompat.Builder(appContext(), channel.id)
    .apply(builder)
    .build()
}

inline fun buildNotification(
  channelId: String,
  channelName: String,
  importance: Int,
  scope: Scope<*> = inject,
  builder: NotificationCompat.Builder.() -> Unit
): Notification = buildNotification(
  channel = NotificationChannel(channelId, channelName, importance),
  builder = builder
)

inline fun NotificationCompat.Builder.uiLauncherIntent(
  requestCode: Int = 1,
  scope: Scope<*> = inject,
  intentBuilder: Intent.() -> Unit = {}
): PendingIntent = PendingIntent.getActivity(
  appContext(),
  requestCode,
  Intent(appContext(), EsActivity::class.java).apply(intentBuilder),
  PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
