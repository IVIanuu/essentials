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

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.coroutines.callbackFlowNoInline
import com.ivianuu.essentials.coroutines.merge
import com.ivianuu.essentials.coroutines.shareIn
import com.ivianuu.essentials.ui.core.DisplayRotation
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.time.seconds

@ApplicationScope
@Single
class DisplayRotationProvider(
    private val app: Application,
    private val screenStateProvider: ScreenStateProvider,
    private val windowManager: WindowManager
) {

    val displayRotation: Flow<DisplayRotation> = screenStateProvider.screenState
        .flatMapLatest { screenState ->
            if (screenState.isOn) {
                merge(
                    listOf(
                        rotationChanges(),
                        configChanges()
                    )
                )
                    .onStart { d { "sub for rotation" } }
                    .onCompletion { d { "dispose rotation" } }
            } else {
                d { "do not observe rotation while screen is off" }
                emptyFlow()
            }
        }
        .map { currentDisplayRotation }
        .distinctUntilChanged()
        .shareIn(scope = GlobalScope, cacheSize = 1, timeout = 1.seconds, tag = "display rotation")

    val currentDisplayRotation: DisplayRotation
        get() = when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> DisplayRotation.PortraitUp
            Surface.ROTATION_90 -> DisplayRotation.LandscapeLeft
            Surface.ROTATION_180 -> DisplayRotation.PortraitDown
            Surface.ROTATION_270 -> DisplayRotation.LandscapeRight
            else -> error("unexpected rotation")
        }

    private fun rotationChanges() = callbackFlowNoInline<DisplayRotation> {
        var currentRotation = currentDisplayRotation

        val listener = object : OrientationEventListener(
            app, SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = currentDisplayRotation
                if (rotation != currentRotation) {
                    it.offer(rotation)
                    currentRotation = rotation
                }
            }
        }

        it.offer(currentRotation)
        listener.enable()

        it.awaitClose { listener.disable() }
    }

    private fun configChanges() = callbackFlowNoInline<Unit> {
        val callbacks = object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                it.offer(Unit)
            }

            override fun onLowMemory() {
            }

            override fun onTrimMemory(level: Int) {
            }
        }
        app.registerComponentCallbacks(callbacks)
        it.awaitClose { app.unregisterComponentCallbacks(callbacks) }
    }

}