/*
 * Copyright 2020 Manuel Wrage
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

import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
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

@Given
fun displayRotation(
    @Given configChanges: () -> Flow<ConfigChange>,
    @Given ioDispatcher: IODispatcher,
    @Given logger: Logger,
    @Given rotationChanges: () -> Flow<RotationChange>,
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
    @Given screenState: () -> Flow<ScreenState>,
    @Given windowManager: @SystemService WindowManager
): @Scoped<AppGivenScope> Flow<DisplayRotation> = flow {
    screenState()
        .flatMapLatest { currentScreenState ->
            if (currentScreenState.isOn) {
                merge(rotationChanges(), configChanges())
                    .onStart { logger.d { "sub for rotation" } }
                    .onCompletion { logger.d { "dispose rotation" } }
            } else {
                logger.d { "do not observe rotation while screen is off" }
                emptyFlow()
            }
        }
        .onStart { emit(Unit) }
        .map { getCurrentDisplayRotation(ioDispatcher, windowManager) }
        .distinctUntilChanged()
        .let { emitAll(it) }
}.shareIn(scope, SharingStarted.WhileSubscribed(1000), 1)
    .distinctUntilChanged()

private suspend fun getCurrentDisplayRotation(
    ioDispatcher: IODispatcher,
    windowManager: WindowManager,
) = withContext(ioDispatcher) {
    when (windowManager.defaultDisplay.rotation) {
        Surface.ROTATION_0 -> DisplayRotation.PORTRAIT_UP
        Surface.ROTATION_90 -> DisplayRotation.LANDSCAPE_LEFT
        Surface.ROTATION_180 -> DisplayRotation.PORTRAIT_DOWN
        Surface.ROTATION_270 -> DisplayRotation.LANDSCAPE_RIGHT
        else -> error("unexpected rotation")
    }
}

typealias RotationChange = Unit

@Given
fun rotationChanges(
    @Given appContext: AppContext,
    @Given mainDispatcher: MainDispatcher,
): Flow<RotationChange> = callbackFlow<RotationChange> {
    val listener = object :
        OrientationEventListener(appContext, SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
            runCatching { offer(RotationChange) }
        }
    }
    listener.enable()
    awaitClose { listener.disable() }
}.flowOn(mainDispatcher)
