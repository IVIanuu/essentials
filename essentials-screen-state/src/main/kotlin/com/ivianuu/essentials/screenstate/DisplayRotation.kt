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
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.deferredFlow
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

enum class DisplayRotation(val isPortrait: Boolean) {
    // 0 degrees
    PortraitUp(true),
    // 90 degrees
    LandscapeLeft(false),
    // 180 degrees
    PortraitDown(true),
    // 270 degrees
    LandscapeRight(false)
}

@Scoped(ApplicationComponent::class)
@Given
fun displayRotation(
    configChanges: () -> ConfigChanges,
    getCurrentDisplayRotation: getCurrentDisplayRotation,
    globalScope: GlobalScope,
    logger: Logger,
    rotationChanges: () -> RotationChanges,
    screenState: () -> Flow<ScreenState>,
): Flow<DisplayRotation> {
    return deferredFlow {
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
            .map { getCurrentDisplayRotation() }
            .distinctUntilChanged()
    }.shareIn(globalScope, SharingStarted.WhileSubscribed(1000), 1)
}

@GivenFun
suspend fun getCurrentDisplayRotation(
    ioDispatcher: IODispatcher,
    windowManager: WindowManager,
): DisplayRotation = withContext(ioDispatcher) {
    when (windowManager.defaultDisplay.rotation) {
        Surface.ROTATION_0 -> DisplayRotation.PortraitUp
        Surface.ROTATION_90 -> DisplayRotation.LandscapeLeft
        Surface.ROTATION_180 -> DisplayRotation.PortraitDown
        Surface.ROTATION_270 -> DisplayRotation.LandscapeRight
        else -> error("unexpected rotation")
    }
}

typealias RotationChanges = Flow<Unit>
@Given
fun rotationChanges(
    applicationContext: ApplicationContext,
    mainDispatcher: MainDispatcher,
): RotationChanges = callbackFlow<Unit> {
    val listener = object :
        OrientationEventListener(applicationContext, SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
            offerSafe(Unit)
        }
    }
    listener.enable()
    awaitClose { listener.disable() }
}.flowOn(mainDispatcher)
