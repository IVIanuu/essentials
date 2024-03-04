/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.app.*
import android.content.*
import androidx.compose.ui.graphics.*
import androidx.core.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.app.*
import com.ivianuu.injekt.*

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
  get() = @Suppress("RestrictedLint") mContext
