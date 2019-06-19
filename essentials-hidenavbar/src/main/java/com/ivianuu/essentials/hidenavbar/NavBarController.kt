/*
 * Copyright 2018 Manuel Wrage
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
import com.ivianuu.essentials.util.BroadcastFactory
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.Observable

/**
 * Handles the state of the navigation bar
 */
@Inject
@ApplicationScope
class NavBarController internal constructor(
    private val app: Application,
    private val broadcastFactory: BroadcastFactory,
    private val displayRotationProvider: DisplayRotationProvider,
    private val keyguardManager: KeyguardManager,
    private val nonSdkInterfacesHelper: NonSdkInterfacesHelper,
    private val overscanHelper: OverscanHelper,
    private val screenStateProvider: ScreenStateProvider
) {

    private val enabledScope = ReusableScope()

    fun setNavBarConfig(config: NavBarConfig) {
        enabledScope.clear()

        if (!config.hidden) {
            setNavBarHiddenInternal(false, config)
            return
        }

        Observable.merge(
            listOf(
                if (config.rotationMode != NavBarRotationMode.NOUGAT)
                    displayRotationProvider.observeRotationChanges().skip(1)
                else Observable.never(),
                if (config.showWhileScreenOff)
                    screenStateProvider.observeScreenStateChanges().skip(1)
                else Observable.never()
            )
        )
            .startWith(Unit)
            .map {
                !config.showWhileScreenOff
                        || (!keyguardManager.isKeyguardLocked && screenStateProvider.isScreenOn)
            }
            .subscribe { setNavBarHiddenInternal(it, config) }
            .disposeBy(enabledScope)

        // force show on shut downs
        broadcastFactory.create(Intent.ACTION_SHUTDOWN)
            .subscribe {
                enabledScope.clear()
                d { "show nav bar because of shutdown" }
                setNavBarHiddenInternal(false, config)
            }
            .disposeBy(enabledScope)
    }

    private fun setNavBarHiddenInternal(hidden: Boolean, config: NavBarConfig) {
        d { "set nav bar hidden: $hidden" }
        try {
            try {
                // ensure that we can access non sdk interfaces
                nonSdkInterfacesHelper.disableNonSdkInterfaceDetection()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val navBarHeight = getNavigationBarHeight()
            val rect = getOverscanRect(if (hidden) -navBarHeight else 0, config)
            overscanHelper.setDisplayOverscan(rect)
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
        NavBarRotationMode.MARSHMALLOW -> {
            when (displayRotationProvider.displayRotation) {
                Surface.ROTATION_90 -> Rect(0, 0, 0, navBarHeight)
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                Surface.ROTATION_270 -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.NOUGAT -> {
            when (displayRotationProvider.displayRotation) {
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.TABLET -> {
            when (displayRotationProvider.displayRotation) {
                Surface.ROTATION_90 -> Rect(navBarHeight, 0, 0, 0)
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                Surface.ROTATION_270 -> Rect(0, 0, navBarHeight, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
    }

}