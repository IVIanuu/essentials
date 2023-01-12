/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

enum class DisplayRotation(val isPortrait: Boolean) {
  // 0 degrees
  PORTRAIT_UP(true),

  // 90 degrees
  LANDSCAPE_LEFT(false),

  // 180 degrees
  PORTRAIT_DOWN(true),

  // 270 degrees
  LANDSCAPE_RIGHT(false);

  @JvmInline value class Provider(val displayRotation: Flow<DisplayRotation>)
}

context(Logger, ScreenState.Provider) @Provide fun displayRotationProvider(
  context: AppContext,
  coroutineContext: MainContext,
  windowManager: @SystemService WindowManager
) = DisplayRotation.Provider(
  screenState
    .flatMapLatest { currentScreenState ->
      if (currentScreenState.isOn) {
        callbackFlow {
          val listener = object :
            OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
              trySend(orientation)
            }
          }
          listener.enable()
          awaitClose { listener.disable() }
        }
          .flowOn(coroutineContext)
          .map { windowManager.getCurrentDisplayRotation() }
          .onStart { log { "sub for rotation" } }
          .onCompletion { log { "dispose rotation" } }
      } else {
        log { "do not observe rotation while screen is off" }
        infiniteEmptyFlow()
      }
    }
    .distinctUntilChanged()
)

private fun WindowManager.getCurrentDisplayRotation() = when (defaultDisplay.rotation) {
  Surface.ROTATION_0 -> DisplayRotation.PORTRAIT_UP
  Surface.ROTATION_90 -> DisplayRotation.LANDSCAPE_LEFT
  Surface.ROTATION_180 -> DisplayRotation.PORTRAIT_DOWN
  Surface.ROTATION_270 -> DisplayRotation.LANDSCAPE_RIGHT
  else -> error("Unexpected rotation")
}
