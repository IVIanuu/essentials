/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide val displayRotationHomeItem = HomeItem("Display rotation") { DisplayRotationKey }

object DisplayRotationKey : Key<Unit>

@Provide fun displayRotationUi(displayInfo: Flow<DisplayInfo>) = SimpleKeyUi<DisplayRotationKey> {
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
