/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.annotation.SuppressLint
import android.app.NotificationManager
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.runningFold
import kotlin.time.Duration.Companion.seconds

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundScreen() }

class ForegroundScreen : Screen<Unit> {
  @Provide companion object {
    @SuppressLint("NewApi")
    @Provide fun ui(
      foregroundManager: ForegroundManager,
      notificationFactory: NotificationFactory
    ) = Ui<ForegroundScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Foreground") } }) {
        var isEnabled by remember { mutableStateOf(false) }

        if (isEnabled)
          LaunchedEffect(true) {
            foregroundManager.startForeground(removeNotification = false) {
              notificationFactory(
                "foreground",
                "Foreground",
                NotificationManager.IMPORTANCE_LOW
              ) {
                setSmallIcon(R.drawable.ic_home)
                setContentTitle("Foreground")

                val count = remember {
                  ticker(1000)
                    .receiveAsFlow()
                    .runningFold(0) { acc, _ -> acc.inc() }
                }.collectAsState(0).value
                setContentText("Current progress $count")
              }
            }
          }

        Button(onClick = { isEnabled = !isEnabled }) {
          Text(if (isEnabled) "Stop foreground" else "Start foreground")
        }
      }
    }
  }
}
