/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.coroutines.timerFlow
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.Notification
import com.ivianuu.essentials.util.NotificationModel
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundScreen() }

class ForegroundScreen : Screen<Unit>

@SuppressLint("NewApi")
@Provide fun foregroundUi(foregroundManager: ForegroundManager) = Ui<ForegroundScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Foreground") }) }
  ) {
    var isEnabled by remember { mutableStateOf(false) }

    if (isEnabled) {
      LaunchedEffect(true) {
        foregroundManager.startForeground(ForegroundNotification)
      }
    }

    Button(onClick = { isEnabled = !isEnabled }) {
      Text(if (isEnabled) "Stop foreground" else "Start foreground")
    }
  }
}

@Serializable object ForegroundNotification : Notification(
  "foreground",
  "Foreground",
  Importance.LOW
)

@Provide fun foregroundNotificationModel(resources: Resources) = Model {
  val count = remember { timerFlow(1.seconds) }.collectAsState(0).value
  NotificationModel<ForegroundNotification>(
    icon = resources(R.drawable.ic_home),
    title = "Foreground",
    text = "Current count $count"
  )
}
