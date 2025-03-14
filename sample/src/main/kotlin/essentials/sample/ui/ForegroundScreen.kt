/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import essentials.sample.R
import android.app.NotificationManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import essentials.*
import essentials.foreground.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.prefs.*
import essentials.util.buildNotification
import essentials.util.uiLauncherIntent
import injekt.*
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundScreen() }

class ForegroundScreen : Screen<Unit>

@Provide @Composable fun ForegroundUi(scope: Scope<*> = inject): Ui<ForegroundScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Foreground") } }) {
    var isEnabled by remember { mutableStateOf(false) }
    var isSecondEnabled by remember { mutableStateOf(false) }
    var removeNotification by remember { mutableStateOf(true) }

    if (isEnabled)
      Foreground(
        id = "foreground",
        removeNotification = removeNotification
      ) {
        buildNotification(
          "foreground",
          "Foreground",
          NotificationManager.IMPORTANCE_LOW
        ) {
          setSmallIcon(R.drawable.ic_launcher_foreground)
          setContentTitle("Foreground")
          setContentText("Current progress ${
            produceState(0) {
              ticker(1000)
                .receiveAsFlow()
                .runningFold(0) { acc, _ -> acc.inc() }
                .collect { value = it }
            }.value
          }")
          setContentIntent(uiLauncherIntent())
        }
      }

    if (isSecondEnabled)
      Foreground(
        id = "foreground2",
        removeNotification = removeNotification
      ) {
        buildNotification(
          "foreground2",
          "Foreground2",
          NotificationManager.IMPORTANCE_LOW
        ) {
          setSmallIcon(R.drawable.ic_launcher_foreground)
          setContentTitle("Foreground2")
          setContentText("Current progress ${
            produceState(0) {
              ticker(2000)
                .receiveAsFlow()
                .runningFold(0) { acc, _ -> acc.inc() }
                .collect { value = it }
            }.value
          }")
          setContentIntent(uiLauncherIntent())
        }
      }

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(onClick = { isEnabled = !isEnabled }) {
        Text(if (isEnabled) "Stop foreground" else "Start foreground")
      }

      Button(onClick = { isSecondEnabled = !isSecondEnabled }) {
        Text(if (isSecondEnabled) "Stop foreground2" else "Start foreground2")
      }

      SwitchListItem(
        value = removeNotification,
        onValueChange = { removeNotification = it },
        headlineContent = { Text("Remove notification") }
      )
    }
  }
}

