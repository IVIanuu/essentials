/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import android.annotation.*
import android.app.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.util.*
import injekt.*
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
      EsScaffold(topBar = { EsAppBar { Text("Foreground") } }) {
        var isEnabled by remember { mutableStateOf(false) }
        var removeNotification by remember { mutableStateOf(true) }

        if (isEnabled)
          foregroundManager.Foreground(removeNotification = removeNotification) {
            notificationFactory.create(
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

          SwitchListItem(
            value = removeNotification,
            onValueChange = { removeNotification = it },
            headlineContent = { Text("Remove notification") }
          )
        }
      }
    }
  }
}
