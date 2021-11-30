/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.annotation.*
import android.app.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundKey }

object ForegroundKey : Key<Unit>

@SuppressLint("NewApi")
@Provide fun foregroundUi(
  foregroundManager: ForegroundManager,
  N: NotificationFactory
) = KeyUi<ForegroundKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Foreground") }) }
  ) {
    val primaryColor = MaterialTheme.colors.primary
    var isEnabled by remember { mutableStateOf(false) }

    if (isEnabled)
      LaunchedEffect(true) {
        var count by mutableStateOf(0)
        par(
          {
            foregroundManager.startForeground(5) {
              ForegroundNotification(primaryColor, count)
            }
          },
          {
            while (isActive) {
              count++
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

private fun ForegroundNotification(
  color: Color,
  count: Int,
  @Inject notificationFactory: NotificationFactory
) = notificationFactory.build(
  "foreground",
  "Foreground",
  NotificationManager.IMPORTANCE_LOW
) {
  setSmallIcon(R.drawable.ic_home)
  setContentTitle("Foreground")
  setContentText("Current progress $count")
  setColor(color.toArgb())
}
