/*
 * Copyright 2020 Manuel Wrage
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

import android.annotation.*
import android.app.*
import android.content.*
import androidx.core.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.flow.*

@Provide fun torchForegroundState(
  state: Flow<TorchState>,
  _: AppContext,
  _: @SystemService NotificationManager,
  _: ResourceProvider,
  _: SystemBuildInfo
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
  @Inject _: ResourceProvider,
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
          1,
          Intent(ACTION_DISABLE_TORCH),
          PendingIntent.FLAG_UPDATE_CURRENT
        )
      )
    }
    .build()
}

private const val NOTIFICATION_CHANNEL_ID = "torch"
