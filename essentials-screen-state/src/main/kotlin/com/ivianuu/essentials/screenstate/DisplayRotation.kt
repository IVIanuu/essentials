/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.screenstate

import android.view.Surface
import android.view.WindowManager
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.IODispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.*
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
  configChanges: () -> Flow<ConfigChange>,
  dispatcher: IODispatcher,
  rotationChanges: () -> Flow<RotationChange>,
  logger: Logger,
  scope: NamedCoroutineScope<AppScope>,
  screenState: () -> Flow<ScreenState>,
  windowManager: @SystemService WindowManager
): @Scoped<AppScope> Flow<DisplayRotation> = flow {
  screenState()
    .flatMapLatest { currentScreenState ->
      if (currentScreenState.isOn) {
        merge(rotationChanges(), configChanges())
          .onStart { log { "sub for rotation" } }
          .onCompletion { log { "dispose rotation" } }
      } else {
        log { "do not observe rotation while screen is off" }
        emptyFlow()
      }
    }
    .onStart { emit(Unit) }
    .map { getCurrentDisplayRotation() }
    .distinctUntilChanged()
    .let { emitAll(it) }
}
  .shareIn(scope, SharingStarted.WhileSubscribed(1000), 1)
  .distinctUntilChanged()

private suspend fun getCurrentDisplayRotation(
  @Inject dispatcher: IODispatcher,
  @Inject windowManager: @SystemService WindowManager,
) = withContext(dispatcher) {
  when (windowManager.defaultDisplay.rotation) {
    Surface.ROTATION_0 -> DisplayRotation.PORTRAIT_UP
    Surface.ROTATION_90 -> DisplayRotation.LANDSCAPE_LEFT
    Surface.ROTATION_180 -> DisplayRotation.PORTRAIT_DOWN
    Surface.ROTATION_270 -> DisplayRotation.LANDSCAPE_RIGHT
    else -> error("Unexpected rotation")
  }
}
