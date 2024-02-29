/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.annotation.*
import android.app.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundScreen() }

class ForegroundScreen : Screen<Unit> {
  @Provide companion object {
    @SuppressLint("NewApi")
    @Provide fun ui(
      foregroundManager: ForegroundManager,
      notificationFactory: NotificationFactory
    ) = Ui<ForegroundScreen> {
      ScreenScaffold(topBar = { AppBar { Text("Foreground") } }) {
        var isEnabled by remember { mutableStateOf(false) }

        if (isEnabled)
          foregroundManager.Foreground(id = "foreground", removeNotification = false) {
            notificationFactory(
              "foreground",
              "Foreground",
              NotificationManager.IMPORTANCE_LOW
            ) {
              setSmallIcon(R.drawable.ic_home)
              setContentTitle("Foreground")
              setContentText("Current progress ${
                ticker(1000)
                  .receiveAsFlow()
                  .runningFold(0) { acc, _ -> acc.inc() }
                  .state(0)
              }")
            }
          }

        Button(onClick = { isEnabled = !isEnabled }) {
          Text(if (isEnabled) "Stop foreground" else "Start foreground")
        }
      }
    }
  }
}
