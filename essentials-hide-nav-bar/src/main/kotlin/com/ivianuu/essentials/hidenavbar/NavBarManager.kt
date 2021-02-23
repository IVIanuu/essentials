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

package com.ivianuu.essentials.hidenavbar

import android.content.Intent
import android.graphics.Rect
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.datastore.android.PrefUpdater
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Handles the state of the navigation bar
 */
@Scoped<AppComponent>
@Given
class NavBarManager(
    @Given private val appContext: AppContext,
    @Given private val broadcastsFactory: BroadcastsFactory,
    @Given private val defaultDispatcher: DefaultDispatcher,
    @Given private val disableNonSdkInterfaceDetection: disableNonSdkInterfaceDetection,
    @Given private val displayRotation: Flow<DisplayRotation>,
    @Given private val globalScope: GlobalScope,
    @Given private val logger: Logger,
    @Given private val screenState: Flow<ScreenState>,
    @Given private val setOverscan: OverscanUpdater,
    @Given private val wasNavBarHidden: Flow<WasNavBarHidden>,
    @Given private val updateWasNavBarHidden: PrefUpdater<WasNavBarHidden>,
) {

    private var job: Job? = null
    private val mutex = Mutex()

    suspend fun setNavBarConfig(config: NavBarConfig) = withContext(defaultDispatcher) {
        logger.d { "set nav bar config $config" }

        mutex.withLock {
            job?.cancel()
            job = null
        }

        if (!config.hidden) {
            logger.d { "not hidden" }
            if (wasNavBarHidden.first()) {
                logger.d { "was hidden" }
                setNavBarConfigInternal(false, config)
                updateWasNavBarHidden { false }
            } else {
                logger.d { "was not hidden" }
            }

            return@withContext
        }

        globalScope.launch {
            coroutineScope {
                val flows = buildList<Flow<*>> {
                    if (config.rotationMode != NavBarRotationMode.Nougat) {
                        this += displayRotation.drop(1)
                    }

                    if (config.showWhileScreenOff) {
                        this += screenState.drop(1)
                    }
                }

                // apply config
                launch {
                    flows.merge()
                        .onStart { emit(Unit) }
                        .map {
                            !config.showWhileScreenOff ||
                                    screenState.first() == ScreenState.Unlocked
                        }
                        .onEach { navBarHidden ->
                            updateWasNavBarHidden { navBarHidden }
                            setNavBarConfigInternal(navBarHidden, config)
                        }
                        .collect()
                }
                // force show on shut downs
                launch {
                    broadcastsFactory(Intent.ACTION_SHUTDOWN)
                        .onEach {
                            mutex.withLock {
                                job?.cancel()
                                job = null
                            }

                            logger.d { "show nav bar because of shutdown" }
                            setNavBarConfigInternal(false, config)
                        }
                        .collect()
                }
            }
        }.also { job ->
            mutex.withLock { this@NavBarManager.job = job }
        }
    }

    private suspend fun setNavBarConfigInternal(hidden: Boolean, config: NavBarConfig) {
        logger.d { "set nav bar hidden config $config hidden $hidden" }
        runKatching {
            runKatching {
                // ensure that we can access non sdk interfaces
                disableNonSdkInterfaceDetection()
            }.onFailure { it.printStackTrace() }

            val navBarHeight = getNavigationBarHeight()
            val rect = getOverscanRect(if (hidden) -navBarHeight else 0, config)
            setOverscan(rect)
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun getNavigationBarHeight(): Int {
        val name =
            if (displayRotation.first().isPortrait) "navigation_bar_height"
            else "navigation_bar_width"
        val id = appContext.resources.getIdentifier(name, "dimen", "android")
        return if (id > 0) appContext.resources.getDimensionPixelSize(id) else 0
    }

    private suspend fun getOverscanRect(
        navBarHeight: Int,
        config: NavBarConfig,
    ): Rect {
        val currentRotation = displayRotation.first()
        return when (config.rotationMode) {
            NavBarRotationMode.Marshmallow -> {
                when (currentRotation) {
                    DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
                    DisplayRotation.LandscapeLeft -> Rect(0, 0, 0, navBarHeight)
                    DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                    DisplayRotation.LandscapeRight -> Rect(0, navBarHeight, 0, 0)
                }
            }
            NavBarRotationMode.Nougat -> {
                when (currentRotation) {
                    DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                    else -> Rect(0, 0, 0, navBarHeight)
                }
            }
            NavBarRotationMode.Tablet -> {
                when (currentRotation) {
                    DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
                    DisplayRotation.LandscapeLeft -> Rect(navBarHeight, 0, 0, 0)
                    DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                    DisplayRotation.LandscapeRight -> Rect(0, 0, navBarHeight, 0)
                }
            }
        }
    }
}
