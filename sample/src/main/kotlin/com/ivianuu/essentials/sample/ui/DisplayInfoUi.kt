/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.screenstate.DisplayInfo
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow

@Provide val displayRotationHomeItem = HomeItem("Display rotation") { DisplayRotationKey() }

class DisplayRotationKey : Key<Unit>

@Provide fun displayRotationUi(displayInfo: Flow<DisplayInfo>) =
  Ui<DisplayRotationKey, Unit> { model ->
    Box(
      modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colors.primary)
        .systemBarStyle(MaterialTheme.colors.primary),
      contentAlignment = Alignment.Center
    ) {
      val currentDisplayInfo by displayInfo.collectAsState(null)
      Text(
        text = currentDisplayInfo.toString(),
        style = MaterialTheme.typography.h4
    )
  }
}
