/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.compositionFlow
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow

enum class DisplayRotation(val isPortrait: Boolean) {
  // 0 degrees
  PORTRAIT_UP(true),

  // 90 degrees
  LANDSCAPE_LEFT(false),

  // 180 degrees
  PORTRAIT_DOWN(true),

  // 270 degrees
  LANDSCAPE_RIGHT(false)
}

@Provide fun displayRotation(
  appContext: AppContext,
  screenStates: Flow<ScreenState>,
  @Inject windowManager: @SystemService WindowManager
): Flow<DisplayRotation> = compositionFlow {
  val screenState = screenStates.collectAsState(ScreenState.OFF).value
  var displayRotation by remember { mutableStateOf(getCurrentDisplayRotation()) }

  if (screenState.isOn)
    DisposableEffect(true) {
      val listener = object : OrientationEventListener(appContext, SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
          displayRotation = getCurrentDisplayRotation()
        }
      }
      listener.enable()
      onDispose { listener.disable() }
    }

  displayRotation
}

private fun getCurrentDisplayRotation(
  @Inject windowManager: @SystemService WindowManager,
) = when (windowManager.defaultDisplay.rotation) {
  Surface.ROTATION_0 -> DisplayRotation.PORTRAIT_UP
  Surface.ROTATION_90 -> DisplayRotation.LANDSCAPE_LEFT
  Surface.ROTATION_180 -> DisplayRotation.PORTRAIT_DOWN
  Surface.ROTATION_270 -> DisplayRotation.LANDSCAPE_RIGHT
  else -> error("Unexpected rotation")
}
