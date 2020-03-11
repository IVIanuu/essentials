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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.android.ui.core.DisplayRotation
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.coroutines.merge
import com.ivianuu.essentials.screenstate.DisplayRotationProvider
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.screenstate.ScreenStateProvider
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.scopes.ReusableScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * Handles the state of the navigation bar
 */
@ApplicationScope
@Single
class NavBarController internal constructor(
    private val app: Application,
    private val broadcastFactory: BroadcastFactory,
    private val displayRotationProvider: DisplayRotationProvider,
    private val dispatchers: AppCoroutineDispatchers,
    private val nonSdkInterfacesHelper: NonSdkInterfacesHelper,
    private val overscanHelper: OverscanHelper,
    private val prefs: NavBarPrefs,
    private val screenStateProvider: ScreenStateProvider
) {

    private val scope = ReusableScope()

    suspend fun setNavBarConfig(config: NavBarConfig) {
        scope.clear()

        if (!config.hidden) {
            if (prefs.wasNavBarHidden.isSet() && prefs.wasNavBarHidden.get()) {
                setNavBarConfigInternal(false, config)
                prefs.wasNavBarHidden.delete()
            }

            return
        }

        val flows = mutableListOf<Flow<*>>().apply {
            if (config.rotationMode != NavBarRotationMode.Nougat) {
                this += displayRotationProvider.displayRotation.drop(1)
            }

            if (config.showWhileScreenOff) {
                this += screenStateProvider.screenState.drop(1)
            }
        }

        // apply config
        merge(flows)
            .onStart { emit(Unit) }
            .map {
                !config.showWhileScreenOff || screenStateProvider.getScreenState() == ScreenState.Unlocked
            }
            .onEach {
                prefs.wasNavBarHidden.set(it)
                setNavBarConfigInternal(it, config)
            }
            .flowOn(dispatchers.default)
            .launchIn(scope.coroutineScope)

        // force show on shut downs
        broadcastFactory.create(Intent.ACTION_SHUTDOWN)
            .onEach {
                scope.clear()
                d { "show nav bar because of shutdown" }
                setNavBarConfigInternal(false, config)
            }
            .flowOn(dispatchers.default)
            .launchIn(scope.coroutineScope)
    }

    private suspend fun setNavBarConfigInternal(hidden: Boolean, config: NavBarConfig) {
        d { "set nav bar hidden: $config" }
        try {
            try {
                // ensure that we can access non sdk interfaces
                nonSdkInterfacesHelper.disableNonSdkInterfaceDetection()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val navBarHeight = getNavigationBarHeight()
            val rect = getOverscanRect(if (hidden) -navBarHeight else 0, config)
            overscanHelper.setOverscan(rect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNavigationBarHeight(): Int {
        val name = if (displayRotationProvider.currentDisplayRotation.isPortrait) "navigation_bar_height"
        else "navigation_bar_width"
        val id = app.resources.getIdentifier(name, "dimen", "android")
        return if (id > 0) app.resources.getDimensionPixelSize(id) else 0
    }

    private fun getOverscanRect(
        navBarHeight: Int,
        config: NavBarConfig
    ) = when (config.rotationMode) {
        NavBarRotationMode.Marshmallow -> {
            when (displayRotationProvider.currentDisplayRotation) {
                DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
                DisplayRotation.LandscapeLeft -> Rect(0, 0, 0, navBarHeight)
                DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                DisplayRotation.LandscapeRight -> Rect(0, navBarHeight, 0, 0)
            }
        }
        NavBarRotationMode.Nougat -> {
            when (displayRotationProvider.currentDisplayRotation) {
                DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.Tablet -> {
            when (displayRotationProvider.currentDisplayRotation) {
                DisplayRotation.PortraitUp -> Rect(0, 0, 0, navBarHeight)
                DisplayRotation.LandscapeLeft -> Rect(navBarHeight, 0, 0, 0)
                DisplayRotation.PortraitDown -> Rect(0, navBarHeight, 0, 0)
                DisplayRotation.LandscapeRight -> Rect(0, 0, navBarHeight, 0)
            }
        }
    }
}
