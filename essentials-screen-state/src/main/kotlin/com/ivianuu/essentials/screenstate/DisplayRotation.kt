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
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.IODispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
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

typealias DisplayRotationFlow = Flow<DisplayRotation>

@Binding(ApplicationComponent::class)
fun displayRotationFlow(
    configChanges: configChanges,
    getCurrentDisplayRotation: getCurrentDisplayRotation,
    globalScope: GlobalScope,
    logger: Logger,
    rotationChanges: rotationChanges,
    screenStateFlow: ScreenStateFlow,
): DisplayRotationFlow {
    return screenStateFlow
        .flatMapLatest { currentScreenState ->
            if (currentScreenState.isOn) {
                merge(rotationChanges(), configChanges())
                    .onStart { logger.d("sub for rotation") }
                    .onCompletion { logger.d("dispose rotation") }
            } else {
                logger.d("do not observe rotation while screen is off")
                emptyFlow()
            }
        }
        .onStart { emit(Unit) }
        .map { getCurrentDisplayRotation() }
        .distinctUntilChanged()
        .shareIn(globalScope, SharingStarted.WhileSubscribed(1000), 1)
}

@FunBinding
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

@FunBinding
fun rotationChanges(applicationContext: ApplicationContext): Flow<Unit> = callbackFlow {
    val listener = object :
        OrientationEventListener(applicationContext, SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
            offerSafe(Unit)
        }
    }
    listener.enable()
    awaitClose { listener.disable() }
}