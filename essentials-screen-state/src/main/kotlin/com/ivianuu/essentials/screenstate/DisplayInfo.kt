/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.ivianuu.essentials.compose.compositionFlow
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.Flow

data class DisplayInfo(
  val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
  val screenWidth: Int = 0,
  val screenHeight: Int = 0
)

@Provide fun displayInfo(
  configChanges: Flow<ConfigChange>,
  displayRotation: Flow<DisplayRotation>,
  windowManager: @SystemService WindowManager
): Flow<DisplayInfo> = compositionFlow {
  val rotation = displayRotation.collectAsState(DisplayRotation.PORTRAIT_UP).value
  remember(configChanges.collectAsState(null).value, rotation) {
    windowManager.defaultDisplay.getRealMetrics(metrics)
    DisplayInfo(
      rotation = rotation,
      screenWidth = metrics.widthPixels,
      screenHeight = metrics.heightPixels
    )
  }
}

private val metrics = DisplayMetrics()
