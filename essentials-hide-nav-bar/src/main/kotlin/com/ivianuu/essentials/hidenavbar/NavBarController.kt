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
import android.app.KeyguardManager
import android.content.Intent
import android.graphics.Rect
import android.view.Surface
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.merge
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.scopes.ReusableScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
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
    private val dispatchers: AppDispatchers,
    private val keyguardManager: KeyguardManager,
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
                this += displayRotationProvider.observeRotationChanges().drop(1)
            }

            if (config.showWhileScreenOff) {
                this += screenStateProvider.observeScreenState().drop(1)
            }

            /** [merge] requires at least so add a dummy here */
            this += emptyFlow<Unit>()
        }

        // apply config
        merge(flows)
            .onStart { emit(Unit) }
            .map {
                !config.showWhileScreenOff ||
                        (!keyguardManager.isKeyguardLocked && screenStateProvider.isScreenOn)
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
        val name = when (displayRotationProvider.displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> "navigation_bar_height"
            else -> "navigation_bar_width"
        }
        val id = app.resources.getIdentifier(name, "dimen", "android")
        return if (id > 0) app.resources.getDimensionPixelSize(id) else 0
    }

    private fun getOverscanRect(
        navBarHeight: Int,
        config: NavBarConfig
    ) = when (config.rotationMode) {
        NavBarRotationMode.Marshmallow -> {
            when (displayRotationProvider.displayRotation) {
                Surface.ROTATION_90 -> Rect(0, 0, 0, navBarHeight)
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                Surface.ROTATION_270 -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.Nougat -> {
            when (displayRotationProvider.displayRotation) {
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.Tablet -> {
            when (displayRotationProvider.displayRotation) {
                Surface.ROTATION_90 -> Rect(navBarHeight, 0, 0, 0)
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                Surface.ROTATION_270 -> Rect(0, 0, navBarHeight, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
    }
}
