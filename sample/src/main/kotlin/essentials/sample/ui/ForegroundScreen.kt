/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import android.app.*
import android.content.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.foreground.*
import essentials.sample.R
import essentials.ui.material.*
import essentials.ui.overlay.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Provide fun foregroundHomeItem(
  foregroundManager: ForegroundManager,
  androidContext: Context = inject
) = HomeItem("Foreground") {
  BottomSheetScreen {
    Subheader { Text("Foreground") }
    var isEnabled by remember { mutableStateOf(false) }
    var isSecondEnabled by remember { mutableStateOf(false) }
    var removeNotification by remember { mutableStateOf(true) }

    if (isEnabled)
      foregroundManager.Foreground(
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
      foregroundManager.Foreground(
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

    SectionSwitch(
      sectionType = SectionType.FIRST,
      checked = isEnabled,
      onCheckedChange = { isEnabled = it },
      title = { Text("Foreground 1") }
    )

    SectionSwitch(
      sectionType = SectionType.MIDDLE,
      checked = isSecondEnabled,
      onCheckedChange = { isSecondEnabled = it },
      title = { Text("Foreground 2") }
    )

    SectionSwitch(
      sectionType = SectionType.LAST,
      checked = removeNotification,
      onCheckedChange = { removeNotification = it },
      title = { Text("Remove notification") }
    )
  }
}
