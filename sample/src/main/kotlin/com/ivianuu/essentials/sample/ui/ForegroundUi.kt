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
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive

@Provide val foregroundHomeItem = HomeItem("Foreground") { ForegroundKey }

object ForegroundKey : Key<Unit>

@SuppressLint("NewApi")
@Provide fun foregroundUi(
  foregroundManager: ForegroundManager,
  N: NotificationFactory,
  scope: NamedCoroutineScope<KeyUiScope>
) = SimpleKeyUi<ForegroundKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Foreground") }) }
  ) {
    val primaryColor = MaterialTheme.colors.primary
    var isEnabled by remember { mutableStateOf(false) }

    if (isEnabled)
      LaunchedEffect(true) {
        foregroundManager.startForeground(
          5,
          flow {
            var i = 0
            while (currentCoroutineContext().isActive) {
              emit(ForegroundNotification(primaryColor, i++))
              delay(1000)
            }
          }
            .stateIn(scope, SharingStarted.Eagerly, ForegroundNotification(primaryColor, 0))
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
