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
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.Surface
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.util.BroadcastFactory
import com.ivianuu.essentials.util.ext.combineLatest
import com.ivianuu.essentials.util.ext.rxIo
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.injekt.annotations.Single
import com.ivianuu.kommon.core.app.doOnConfigurationChanged
import com.ivianuu.kommon.core.content.isScreenOn
import com.ivianuu.kommon.core.content.rotation
import com.ivianuu.kprefs.rx.observable
import com.ivianuu.rxjavaktx.emptyObservable
import com.ivianuu.rxjavaktx.observable
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.timberktx.d
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

/**
 * Handles the state of the navigation bar
 */
@Single(ApplicationScope::class)
class NavBarController(
    private val app: Application,
    private val broadcastFactory: BroadcastFactory,
    private val keyguardManager: KeyguardManager,
    private val nonSdkInterfacesHelper: NonSdkInterfacesHelper,
    private val prefs: NavBarPrefs,
    private val overscanHelper: OverscanHelper
) : AppService() {

    private val enabledScope = ReusableScope()

    override fun start() {
        prefs.manageNavBar.observable()
            .subscribe(this::updateEnabledState)
            .disposeBy(scope)
    }

    private fun updateEnabledState(enabled: Boolean) {
        enabledScope.clear()

        if (!enabled) {
            // only force the nav bar to be shown if it was hidden by us
            if (prefs.wasNavBarHidden.isSet && prefs.wasNavBarHidden.get()) {
                d { "force show nav bar because it was hidden by us" }
                // force showing the nav bar
                updateNavBarState(false)
            }

            prefs.wasNavBarHidden.delete()

            return
        }

        Observables
            .combineLatest(
                prefChanges(),
                configChanges().startWith(Unit),
                rotationChangesWhileScreenOn().startWith(Unit),
                screenState()
            )
            .observeOn(rxIo)
            .map {
                prefs.navBarHidden.get()
                        && (!prefs.showNavBarScreenOff.get()
                        || (!keyguardManager.isKeyguardLocked && app.isScreenOn))
            }
            .subscribe(this::updateNavBarState)
            .disposeBy(enabledScope)

        // force show on shut downs
        broadcastFactory.create(Intent.ACTION_SHUTDOWN)
            .subscribe {
                d { "show nav bar because of shutdown" }
                updateNavBarState(false)
            }
            .disposeBy(enabledScope)
    }

    private fun updateNavBarState(hide: Boolean) {
        d { "update nav bar state: hide $hide" }
        try {
            try {
                // ensure that we can access non sdk interfaces
                nonSdkInterfacesHelper.disableNonSdkInterfaceDetection()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val navBarHeight =
                getNavigationBarHeight() - (if (prefs.fullOverscan.get()) 0 else OVERSCAN_LEFT_PIXELS)
            val rect = getOverscanRect(if (hide) -navBarHeight else 0)
            overscanHelper.setDisplayOverscan(rect)
            prefs.wasNavBarHidden.set(hide)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNavigationBarHeight(): Int {
        val name = when (app.rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> "navigation_bar_height"
            else -> "navigation_bar_width"
        }
        val id = app.resources.getIdentifier(name, "dimen", "android")
        return if (id > 0) {
            app.resources.getDimensionPixelSize(id)
        } else 0
    }

    private fun getOverscanRect(navBarHeight: Int) = when (prefs.rotationMode.get()) {
        NavBarRotationMode.MARSHMALLOW -> {
            when (app.rotation) {
                Surface.ROTATION_90 -> Rect(0, 0, 0, navBarHeight)
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                Surface.ROTATION_270 -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.NOUGAT -> {
            when (app.rotation) {
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
        NavBarRotationMode.TABLET -> {
            when (app.rotation) {
                Surface.ROTATION_90 -> Rect(navBarHeight, 0, 0, 0)
                Surface.ROTATION_180 -> Rect(0, navBarHeight, 0, 0)
                Surface.ROTATION_270 -> Rect(0, 0, navBarHeight, 0)
                else -> Rect(0, 0, 0, navBarHeight)
            }
        }
    }

    private fun prefChanges(): Observable<Unit> {
        return Observables.combineLatest(
            prefs.navBarHidden.observable(),
            prefs.fullOverscan.observable(),
            prefs.rotationMode.observable(),
            prefs.showNavBarScreenOff.observable()
        ).map { Unit }
    }

    private fun rotationChangesWhileScreenOn(): Observable<Unit> {
        return screenState()
            .switchMap {
                if (it && !keyguardManager.isKeyguardLocked) {
                    rotationChanges()
                        .doOnSubscribe { d { "sub for rotation" } }
                        .doOnDispose { d { "dispose rotation" } }
                } else {
                    d { "do not observe rotation while screen is off" }
                    emptyObservable()
                }
            }
            .map { Unit }
    }

    private fun rotationChanges() = observable<Int> { e ->
        var currentRotation = app.rotation

        val listener = object : OrientationEventListener(
            app, SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = app.rotation
                if (rotation != currentRotation) {
                    e.onNext(rotation)
                    currentRotation = rotation
                }
            }

        }

        e.setCancellable(listener::disable)

        listener.enable()

        e.onNext(currentRotation)
    }

    private fun configChanges() = observable<Unit> { e ->
        val callbacks = app.doOnConfigurationChanged { e.onNext(Unit) }
        e.setCancellable { app.unregisterComponentCallbacks(callbacks) }
    }

    private fun screenState(): Observable<Boolean> {
        return broadcastFactory.create(
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_USER_PRESENT
        )
            .map { app.isScreenOn }
            .startWith(app.isScreenOn)
    }

    companion object {
        private const val OVERSCAN_LEFT_PIXELS = 1
    }
}