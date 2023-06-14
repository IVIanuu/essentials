/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.screenstate.DisplayInfo
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow

@Provide val displayRotationHomeItem = HomeItem("Display rotation") { DisplayRotationScreen() }

class DisplayRotationScreen : Screen<Unit>

@Provide fun displayRotationUi(displayInfo: Flow<DisplayInfo>) =
  Ui<DisplayRotationScreen, Unit> {
    Box(
      modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .systemBarStyle(MaterialTheme.colorScheme.primary),
      contentAlignment = Alignment.Center
    ) {
      val currentDisplayInfo by displayInfo.collectAsState(null)
      Text(
        text = currentDisplayInfo.toString(),
        style = MaterialTheme.typography.headlineMedium
    )
  }
}
