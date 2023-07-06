/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ui.AppColors
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

fun interface NotificationFactory {
  operator fun invoke(
    channel: NotificationChannel,
    builder: NotificationCompat.Builder.() -> Unit
  ): Notification

  operator fun invoke(
    channelId: String,
    channelName: String,
    importance: Int,
    builder: NotificationCompat.Builder.() -> Unit
  ): Notification = invoke(NotificationChannel(channelId, channelName, importance), builder)
}

inline val NotificationCompat.Builder.context: Context
  get() = @Suppress("RestrictedLint") mContext

@Provide fun notificationFactory(
  appContext: AppContext,
  appColors: AppColors,
  notificationManager: @SystemService NotificationManager
) = NotificationFactory { channel, builder ->
  notificationManager.createNotificationChannel(channel)
  NotificationCompat.Builder(appContext, channel.id)
    .apply { color = appColors.primary.toArgb() }
    .apply(builder)
    .build()
}
