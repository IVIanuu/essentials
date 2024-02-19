/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.hardware.*
import android.view.*
import androidx.compose.runtime.*
import app.cash.molecule.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

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
  deviceScreenManager: DeviceScreenManager,
  windowManager: @SystemService WindowManager
): Flow<DisplayRotation> = moleculeFlow(RecompositionMode.Immediate) {
  val screenState = deviceScreenManager.screenState.collect(ScreenState.OFF)
  var displayRotation by remember { mutableStateOf(getCurrentDisplayRotation(windowManager)) }

  if (screenState.isOn)
    DisposableEffect(true) {
      val listener = object : OrientationEventListener(appContext, SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
          displayRotation = getCurrentDisplayRotation(windowManager)
        }
      }
      listener.enable()
      onDispose { listener.disable() }
    }

  displayRotation
}

private fun getCurrentDisplayRotation(
  windowManager: @SystemService WindowManager,
) = when (windowManager.defaultDisplay.rotation) {
  Surface.ROTATION_0 -> DisplayRotation.PORTRAIT_UP
  Surface.ROTATION_90 -> DisplayRotation.LANDSCAPE_LEFT
  Surface.ROTATION_180 -> DisplayRotation.PORTRAIT_DOWN
  Surface.ROTATION_270 -> DisplayRotation.LANDSCAPE_RIGHT
  else -> error("Unexpected rotation")
}
