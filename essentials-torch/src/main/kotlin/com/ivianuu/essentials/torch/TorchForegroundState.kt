/*
 * Copyright 2021 Manuel Wrage
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
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.foreground.ForegroundState
import com.ivianuu.essentials.foreground.ForegroundState.Background
import com.ivianuu.essentials.foreground.ForegroundState.Foreground
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Provide fun torchForegroundState(
  state: Flow<TorchState>,
  context: AppContext,
  notificationManager: @SystemService NotificationManager,
  rp: ResourceProvider,
  systemBuildInfo: SystemBuildInfo
): Flow<ForegroundState> = state
  .map { torchEnabled ->
    if (torchEnabled) Foreground(createTorchNotification())
    else Background
  }

@SuppressLint("NewApi")
private fun createTorchNotification(
  @Inject context: AppContext,
  @Inject notificationManager: @SystemService NotificationManager,
  @Inject systemBuildInfo: SystemBuildInfo,
  @Inject rp: ResourceProvider,
): Notification {
  if (systemBuildInfo.sdk >= 26) {
    notificationManager.createNotificationChannel(
      NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        loadResource<String>(R.string.es_notif_channel_torch),
        NotificationManager.IMPORTANCE_LOW
      )
    )
  }

  return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
    .apply {
      setAutoCancel(true)
      setSmallIcon(R.drawable.es_ic_flash_on)
      setContentTitle(loadResource<String>(R.string.es_notif_title_torch))
      setContentText(loadResource<String>(R.string.es_notif_text_torch))
      setContentIntent(
        PendingIntent.getBroadcast(
          context,
          87,
          Intent(ACTION_DISABLE_TORCH),
          PendingIntent.FLAG_UPDATE_CURRENT
        )
      )
    }
    .build()
}

private const val NOTIFICATION_CHANNEL_ID = "torch"
