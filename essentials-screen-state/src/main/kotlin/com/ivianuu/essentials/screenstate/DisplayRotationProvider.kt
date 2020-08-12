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

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.ui.core.DisplayRotation
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationStorage
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given
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

@Given(ApplicationStorage::class)
class DisplayRotationProvider {

    val displayRotation: Flow<DisplayRotation> = screenState
        .flatMapLatest { screenState ->
            if (screenState.isOn) {
                merge(
                    rotationChanges(),
                    configChanges()
                )
                    .onStart { d { "sub for rotation" } }
                    .onCompletion { d { "dispose rotation" } }
            } else {
                d { "do not observe rotation while screen is off" }
                emptyFlow()
            }
        }
        .onStart { emit(Unit) }
        .map { getCurrentDisplayRotation() }
        .distinctUntilChanged()
        .shareIn(
            scope = globalScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000)
        )

    private suspend fun getCurrentDisplayRotation(): DisplayRotation =
        withContext(dispatchers.default) {
            when (given<WindowManager>().defaultDisplay.rotation) {
                Surface.ROTATION_0 -> DisplayRotation.PortraitUp
                Surface.ROTATION_90 -> DisplayRotation.LandscapeLeft
                Surface.ROTATION_180 -> DisplayRotation.PortraitDown
                Surface.ROTATION_270 -> DisplayRotation.LandscapeRight
                else -> error("unexpected rotation")
            }
        }

    private fun rotationChanges() = callbackFlow<Unit> {
        val listener = object :
            OrientationEventListener(applicationContext, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                offer(Unit)
            }
        }
        listener.enable()
        awaitClose { listener.disable() }
    }

    private fun configChanges() = callbackFlow<Unit> {
        val callbacks = object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                offer(Unit)
            }

            override fun onLowMemory() {
            }

            override fun onTrimMemory(level: Int) {
            }
        }
        applicationContext.registerComponentCallbacks(callbacks)
        awaitClose { applicationContext.unregisterComponentCallbacks(callbacks) }
    }
}
