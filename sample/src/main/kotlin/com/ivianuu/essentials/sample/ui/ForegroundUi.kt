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

package com.ivianuu.essentials.sample.ui

import android.annotation.*
import android.app.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundKey }

object ForegroundKey : Key<Nothing>

@SuppressLint("NewApi")
@Provide
fun foregroundUi(
  foregroundState: ForegroundScreenState,
  context: AppContext,
  notificationManager: @SystemService NotificationManager,
  systemBuildInfo: SystemBuildInfo
): KeyUi<ForegroundKey> = {
  val currentForegroundState by foregroundState.collectAsState()

  DisposableEffect(true) {
    onDispose {
      foregroundState.value = Background
    }
  }

  val primaryColor = MaterialTheme.colors.primary

  Scaffold(
    topBar = { TopAppBar(title = { Text("Foreground") }) }
  ) {
    var count by remember(currentForegroundState is Foreground) { mutableStateOf(0) }

    if (currentForegroundState is Foreground) {
      LaunchedEffect(true) {
        while (isActive) {
          count++
          foregroundState.value = Foreground(createForegroundNotification(primaryColor, count))
          delay(1000)
        }
      }
    }

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (currentForegroundState is Foreground) {
        Text(
          text = "Current progress $count",
          style = MaterialTheme.typography.h5
        )
      }
      Spacer(Modifier.height(8.dp))
      Button(
        onClick = {
          foregroundState.value = (if (currentForegroundState is Foreground) Background
          else Foreground(createForegroundNotification(primaryColor, count)))
        }
      ) {
        Text(
          if (currentForegroundState is Foreground) {
            "Stop foreground"
          } else {
            "Start foreground"
          }
        )
      }
    }
  }
}

typealias ForegroundScreenState = MutableStateFlow<ForegroundState>

@Provide val foregroundScreenState: @Scoped<AppScope> ForegroundScreenState
  get() = MutableStateFlow(Background)

@SuppressLint("NewApi") private fun createForegroundNotification(
  color: Color,
  count: Int,
  @Inject context: AppContext,
  @Inject notificationManager: @SystemService NotificationManager,
  @Inject systemBuildInfo: SystemBuildInfo
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
