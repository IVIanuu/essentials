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
import android.content.Intent
import android.graphics.Rect
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.screenstate.DisplayRotationProvider
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.screenstate.ScreenStateProvider
import com.ivianuu.essentials.ui.core.DisplayRotation
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Handles the state of the navigation bar
 */
@Given(ApplicationComponent::class)
class NavBarManager {

    private var job: Job? = null
    private val mutex = Mutex()

    private val displayRotationProvider = given<DisplayRotationProvider>()
    private val prefs = given<NavBarPrefs>()
    private val screenStateProvider = given<ScreenStateProvider>()

    suspend fun setNavBarConfig(config: NavBarConfig) = withContext(dispatchers.default) {
        d { "set nav bar config $config" }

        mutex.withLock {
            job?.cancel()
            job = null
        }

        if (!config.hidden) {
            d { "not hidden" }
            if (prefs.wasNavBarHidden.data.first()) {
                d { "was hidden" }
                setNavBarConfigInternal(false, config)
                prefs.wasNavBarHidden.updateData { false }
            } else {
                d { "was not hidden" }
            }

            return@withContext
        }

        globalScope.launch {
            buildList<Deferred<*>> {
                val flows = buildList<Flow<*>> {
                    if (config.rotationMode != NavBarRotationMode.Nougat) {
                        this += displayRotationProvider.displayRotation.drop(1)
                    }

                    if (config.showWhileScreenOff) {
                        this += screenStateProvider.screenState.drop(1)
                    }
                }

                // apply config
                this += async {
                    flows.merge()
                        .onStart { emit(Unit) }
                        .map {
                            !config.showWhileScreenOff ||
                                    screenStateProvider.screenState.first() == ScreenState.Unlocked
                        }
                        .onEach { navBarHidden ->
                            prefs.wasNavBarHidden.updateData { navBarHidden }
                            setNavBarConfigInternal(navBarHidden, config)
                        }
                        .collect()
                }
                this += async {
                    // force show on shut downs
                    BroadcastFactory.create(Intent.ACTION_SHUTDOWN)
                        .onEach {
                            mutex.withLock {
                                job?.cancel()
                                job = null
                            }

                            d { "show nav bar because of shutdown" }
                            setNavBarConfigInternal(false, config)
                        }
                        .collect()
                }
            }.awaitAll()
        }.also { job ->
            mutex.withLock { this@NavBarManager.job = job }
        }
    }

    private suspend fun setNavBarConfigInternal(hidden: Boolean, config: NavBarConfig) {
        d { "set nav bar hidden config $config hidden $hidden" }
        try {
            try {
                // ensure that we can access non sdk interfaces
                disableNonSdkInterfaceDetection()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val navBarHeight = getNavigationBarHeight()
            val rect = getOverscanRect(if (hidden) -navBarHeight else 0, config)
            setOverscan(rect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun getNavigationBarHeight(): Int {
        val name =
            if (displayRotationProvider.displayRotation.first().isPortrait) "navigation_bar_height"
            else "navigation_bar_width"
        val id = applicationContext.resources.getIdentifier(name, "dimen", "android")
        return if (id > 0) applicationContext.resources.getDimensionPixelSize(id) else 0
    }

    private suspend fun getOverscanRect(
        navBarHeight: Int,
        config: NavBarConfig
    ) = when (config.rotationMode) {
        NavBarRotationMode.Marshmallow -> {
            when (displayRotationProvider.displayRotation.first()) {
                DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
                DisplayRotation.LandscapeLeft -> Rect(0, 0, 0, navBarHeight)
                DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                DisplayRotation.LandscapeRight -> Rect(0, navBarHeight, 0, 0)
            }
        }
        NavBarRotationMode.Nougat -> {
            when (displayRotationProvider.displayRotation.first()) {
                DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.Tablet -> {
            when (displayRotationProvider.displayRotation.first()) {
                DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
                DisplayRotation.LandscapeLeft -> Rect(navBarHeight, 0, 0, 0)
                DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                DisplayRotation.LandscapeRight -> Rect(0, 0, navBarHeight, 0)
            }
        }
    }
}
