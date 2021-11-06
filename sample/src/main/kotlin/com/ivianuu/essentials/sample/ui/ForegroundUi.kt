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

package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.coroutines.mapState
import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundKey }

object ForegroundKey : Key<Unit>

@SuppressLint("NewApi")
@Provide
fun foregroundUi(
  context: AppContext,
  foregroundManager: ForegroundManager,
  notificationManager: @SystemService NotificationManager,
  systemBuildInfo: SystemBuildInfo
): KeyUi<ForegroundKey> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Foreground") }) }
  ) {
    val primaryColor = MaterialTheme.colors.primary
    var isEnabled by remember { mutableStateOf(false) }

    if (isEnabled)
      LaunchedEffect(true) {
        val count = MutableStateFlow(0)
        par(
          {
            foregroundManager.startForeground(
              5,
              count
                .mapState { createForegroundNotification(primaryColor, it) }
            )
          },
          {
            while (isActive) {
              count.value++
              delay(1000)
            }
          }
        )
      }

    Button(onClick = { isEnabled = !isEnabled }) {
      Text(if (isEnabled) "Stop foreground" else "Start foreground")
    }
  }
}

@SuppressLint("NewApi") private fun createForegroundNotification(
  color: Color,
  count: Int,
  @Inject context: AppContext,
  notificationManager: @SystemService NotificationManager,
  systemBuildInfo: SystemBuildInfo
): Notification {
  if (systemBuildInfo.sdk >= 26) {
    notificationManager.createNotificationChannel(
      NotificationChannel(
        "foreground", "Foreground",
        NotificationManager.IMPORTANCE_LOW
      )
    )
  }
  return NotificationCompat.Builder(context, "foreground")
    .setSmallIcon(R.drawable.ic_home)
    .setContentTitle("Foreground")
    .setContentText("Current progress $count")
    .setColor(color.toArgb())
    .build()
}
