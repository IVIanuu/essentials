/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.invoke
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.IOContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

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
  ioContext: IOContext,
  logger: Logger,
  screenState: () -> Flow<ScreenState>,
  windowManager: @SystemService WindowManager
): Flow<DisplayRotation> = flow {
  screenState()
    .flatMapLatest { currentScreenState ->
      if (currentScreenState.isOn) {
        callbackFlow {
          val listener = object :
            OrientationEventListener(appContext, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
              trySend(orientation)
            }
          }
          listener.enable()
          awaitClose { listener.disable() }
        }
      } else {
        logger { "do not observe rotation while screen is off" }
        emptyFlow()
      }
    }
    .onStart<Any?> { emit(Unit) }
    .map { getCurrentDisplayRotation() }
    .distinctUntilChanged()
    .let { emitAll(it) }
}

private suspend fun getCurrentDisplayRotation(
  @Inject context: IOContext,
  @Inject windowManager: @SystemService WindowManager,
) = withContext(context) {
  when (windowManager.defaultDisplay.rotation) {
    Surface.ROTATION_0 -> DisplayRotation.PORTRAIT_UP
    Surface.ROTATION_90 -> DisplayRotation.LANDSCAPE_LEFT
    Surface.ROTATION_180 -> DisplayRotation.PORTRAIT_DOWN
    Surface.ROTATION_270 -> DisplayRotation.LANDSCAPE_RIGHT
    else -> error("Unexpected rotation")
  }
}
