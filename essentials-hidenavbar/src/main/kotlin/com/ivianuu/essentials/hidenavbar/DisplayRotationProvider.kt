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

package com.ivianuu.essentials.hidenavbar

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.WindowManager
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.merge
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

@Factory
internal class DisplayRotationProvider(
    private val app: Application,
    private val screenStateProvider: ScreenStateProvider,
    private val windowManager: WindowManager
) {

    val displayRotation: Int get() = windowManager.defaultDisplay.rotation

    fun observeRotationChanges(): Flow<Int> {
        return screenStateProvider.observeScreenState()
            .flatMapLatest {
                if (it) {
                    merge(rotationChanges(), configChanges())
                        .onStart { d { "sub for rotation" } }
                        .onCompletion { d { "dispose rotation" } }
                } else {
                    d { "do not observe rotation while screen is off" }
                    emptyFlow()
                }
            }
            .map { displayRotation }
            .onStart { emit(displayRotation) }
    }

    private fun rotationChanges() = callbackFlow {
        var currentRotation = displayRotation

        val listener = object : OrientationEventListener(
            app, SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = displayRotation
                if (rotation != currentRotation) {
                    offer(rotation)
                    currentRotation = rotation
                }
            }
        }

        offer(currentRotation)
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
        app.registerComponentCallbacks(callbacks)
        awaitClose { app.unregisterComponentCallbacks(callbacks) }
    }

}