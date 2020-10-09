/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.ui.core.DisplayRotation
import com.ivianuu.essentials.util.IODispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@FunBinding
fun displayRotation(
    configChanges: configChanges,
    getCurrentDisplayRotation: getCurrentDisplayRotation,
    logger: Logger,
    rotationChanges: rotationChanges,
    screenState: screenState,
): Flow<DisplayRotation> {
    return screenState()
        .flatMapLatest { currentScreenState ->
            if (currentScreenState.isOn) {
                merge(
                    rotationChanges(),
                    configChanges()
                )
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

