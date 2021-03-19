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

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.android.prefs.PrefAction
import com.ivianuu.essentials.android.prefs.update
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@Given
fun navBarManager(
    @Given appContext: AppContext,
    @Given broadcastsFactory: BroadcastsFactory,
    @Given displayRotation: Flow<DisplayRotation>,
    @Given navBarFeatureSupported: NavBarFeatureSupported,
    @Given navBarConfig: Flow<NavBarConfig>,
    @Given logger: Logger,
    @Given nonSdkInterfaceDetectionDisabler: NonSdkInterfaceDetectionDisabler,
    @Given permissionState: Flow<NavBarPermissionState>,
    @Given screenState: Flow<ScreenState>,
    @Given setOverscan: OverscanUpdater,
    @Given wasNavBarHidden: Flow<WasNavBarHidden>,
    @Given wasNavBarHiddenCollector: Collector<PrefAction<WasNavBarHidden>>
): ScopeWorker<AppGivenScope> = worker@ {
    if (!navBarFeatureSupported) return@worker
    permissionState
        .flatMapLatest {
            if (it) {
                broadcastsFactory(Intent.ACTION_SHUTDOWN)
                    .map { true }
                    .onStart { emit(false) }
                    .flatMapLatest { currentSystemShutdown ->
                        // force show on shut downs
                        if (currentSystemShutdown) {
                            logger.d { "system shutdown force show nav bar" }
                            flowOf(NavBarConfig(hidden = false))
                        } else {
                            navBarConfig
                        }
                    }
            } else {
                infiniteEmptyFlow()
            }
        }
        .flatMapLatest { currentConfig ->
            if (currentConfig.hidden) {
                combine(
                    displayRotation,
                    screenState
                ) { currentDisplayRotation, currentScreenState ->
                    NavBarState(
                        config = currentConfig,
                        rotation = currentDisplayRotation,
                        screenState = currentScreenState
                    )
                }
                    .onEach {
                        wasNavBarHiddenCollector.update { true }
                    }
            } else {
                if (!wasNavBarHidden.first()) {
                    infiniteEmptyFlow()
                } else {
                    flowOf(
                        NavBarState(
                            config = currentConfig,
                            screenState = ScreenState.Off,
                            rotation = DisplayRotation.PortraitUp
                        )
                    )
                }
            }
        }
        .collect { it.apply(appContext, nonSdkInterfaceDetectionDisabler, logger, setOverscan) }
}

private data class NavBarState(
    val config: NavBarConfig,
    val screenState: ScreenState,
    val rotation: DisplayRotation
) {
    val hidden: Boolean
        get() = config.hidden && (!config.showWhileScreenOff || screenState == ScreenState.Unlocked)
}

private suspend fun NavBarState.apply(
    context: Context,
    disableNonSdkInterfaceDetection: NonSdkInterfaceDetectionDisabler,
    logger: Logger,
    setOverscan: OverscanUpdater
) {
    logger.d { "apply nav bar state $this" }
    runCatching {
        runCatching {
            // ensure that we can access non sdk interfaces
            disableNonSdkInterfaceDetection()
        }.onFailure { it.printStackTrace() }

        val navBarHeight = getNavigationBarHeight(context, rotation)
        val rect = getOverscanRect(if (hidden) -navBarHeight else 0, config.rotationMode, rotation)
        setOverscan(rect)
    }.onFailure { it.printStackTrace() }
}

private fun getNavigationBarHeight(
    context: Context,
    rotation: DisplayRotation
): Int {
    val name = if (rotation.isPortrait) "navigation_bar_height"
    else "navigation_bar_width"
    val id = context.resources.getIdentifier(name, "dimen", "android")
    return if (id > 0) context.resources.getDimensionPixelSize(id) else 0
}

private fun getOverscanRect(
    navBarHeight: Int,
    rotationMode: NavBarRotationMode,
    rotation: DisplayRotation
): Rect = when (rotationMode) {
    NavBarRotationMode.Marshmallow -> {
        when (rotation) {
            DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
            DisplayRotation.LandscapeLeft -> Rect(0, 0, 0, navBarHeight)
            DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
            DisplayRotation.LandscapeRight -> Rect(0, navBarHeight, 0, 0)
        }
    }
    NavBarRotationMode.Nougat -> {
        when (rotation) {
            DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
            else -> Rect(0, 0, 0, navBarHeight)
        }
    }
    NavBarRotationMode.Tablet -> {
        when (rotation) {
            DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
            DisplayRotation.LandscapeLeft -> Rect(navBarHeight, 0, 0, 0)
            DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
            DisplayRotation.LandscapeRight -> Rect(0, 0, navBarHeight, 0)
        }
    }
}

