/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import androidx.compose.ui.graphics.*
import androidx.core.app.*
import essentials.*
import essentials.app.EsActivity
import essentials.ui.app.*
import injekt.*

@Provide class NotificationFactory(
  @PublishedApi internal val appContext: AppContext,
  private val appColors: AppColors,
  @PublishedApi internal val notificationManager: @SystemService NotificationManager
) {
  inline fun create(
    channel: NotificationChannel,
    builder: NotificationCompat.Builder.() -> Unit
  ): Notification {
    notificationManager.createNotificationChannel(channel)
    return NotificationCompat.Builder(appContext, channel.id)
      .defaults()
      .apply(builder)
      .build()
  }

  @PublishedApi internal fun NotificationCompat.Builder.defaults() = apply {
    color = appColors.primary.toArgb()
  }

  inline fun create(
    channelId: String,
    channelName: String,
    importance: Int,
    builder: NotificationCompat.Builder.() -> Unit
  ): Notification = create(NotificationChannel(channelId, channelName, importance), builder)
}

inline val NotificationCompat.Builder.context: Context
  @SuppressLint("RestrictedApi") get() = mContext

inline fun NotificationCompat.Builder.uiLauncherIntent(
  requestCode: Int = 1,
  intentBuilder: Intent.() -> Unit = {}
): PendingIntent = PendingIntent.getActivity(
  context,
  requestCode,
  Intent(context, EsActivity::class.java).apply(intentBuilder),
  PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
