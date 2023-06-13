/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
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
import com.ivianuu.essentials.coroutines.timerFlow
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundKey() }

class ForegroundKey : Key<Unit>

@SuppressLint("NewApi")
@Provide fun foregroundUi(
  foregroundManager: ForegroundManager,
  notificationFactory: NotificationFactory
) = Ui<ForegroundKey, Unit> { model ->
  Scaffold(
    topBar = { TopAppBar(title = { Text("Foreground") }) }
  ) {
    val primaryColor = MaterialTheme.colors.primary
    var isEnabled by remember { mutableStateOf(false) }

    if (isEnabled)
      LaunchedEffect(true) {
        val notifications = timerFlow(1.seconds)
          .map { ForegroundNotification(primaryColor, it.toInt()) }
          .stateIn(this, SharingStarted.Eagerly, ForegroundNotification(primaryColor, 0))
        foregroundManager.runInForeground(notifications.value) {
          notifications.collect { updateNotification(it) }
        }
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
) = notificationFactory(
  "foreground",
  "Foreground",
  NotificationManager.IMPORTANCE_LOW
) {
  setSmallIcon(R.drawable.ic_home)
  setContentTitle("Foreground")
  setContentText("Current progress $count")
  setColor(color.toArgb())
}
