/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

interface NotificationFactory {
  fun build(
    channelId: String,
    channelName: String,
    importance: Int,
    builder: NotificationCompat.Builder.() -> Unit
  ): Notification
}

inline val NotificationCompat.Builder.context: Context
  get() = @Suppress("RestrictedLint") mContext

@Provide class NotificationFactoryImpl(
  private val context: AppContext,
  private val systemBuildInfo: SystemBuildInfo,
  private val notificationManager: @SystemService NotificationManager
) : NotificationFactory {
  @SuppressLint("NewApi")
  override fun build(
    channelId: String,
    channelName: String,
    importance: Int,
    builder: NotificationCompat.Builder.() -> Unit
  ): Notification {
    if (systemBuildInfo.sdk >= 26) {
      notificationManager.createNotificationChannel(
        NotificationChannel(
          channelId,
          channelName,
          NotificationManager.IMPORTANCE_LOW
        )
      )
    }

    return NotificationCompat.Builder(context, channelId)
      .apply(builder)
      .build()
  }
}
