/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.inject

fun interface NotificationFactory {
  fun buildNotification(
    channelId: String,
    channelName: String,
    importance: Int,
    builder: context(NotificationCompat.Builder) () -> Unit
  ): Notification
}

inline val NotificationCompat.Builder.context: Context
  get() = @Suppress("RestrictedLint") mContext

context(AppContext, NotificationManager)
    @Provide fun notificationFactory() =
  NotificationFactory { channelId, channelName, importance, builder ->
    createNotificationChannel(
      NotificationChannel(
        channelId,
        channelName,
        importance
      )
    )

    NotificationCompat.Builder(inject(), channelId)
      .apply(builder)
      .build()
  }
