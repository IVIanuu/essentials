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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.util.BroadcastFactory
import com.ivianuu.essentials.util.ext.combineLatest
import com.ivianuu.essentials.util.ext.observable
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.kommon.core.app.doOnConfigurationChanged
import com.ivianuu.kprefs.rx.asObservable
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

/**
 * Handles the state of the navigation bar
 */
@Inject @ApplicationScope
class NavBarController(
    private val app: Application,
    private val broadcastFactory: BroadcastFactory,
    private val displayRotationProvider: DisplayRotationProvider,
    private val keyguardManager: KeyguardManager,
    private val nonSdkInterfacesHelper: NonSdkInterfacesHelper,
    private val prefs: NavBarPrefs,
    private val overscanHelper: OverscanHelper,
    private val screenStateProvider: ScreenStateProvider
) : AppService() {

    private val enabledScope = ReusableScope()

    override fun start() {
        prefs.manageNavBar.asObservable()
            .subscribe { updateEnabledState(it) }
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
            .map {
                prefs.navBarHidden.get()
                        && (!prefs.showNavBarScreenOff.get()
                        || (!keyguardManager.isKeyguardLocked && screenStateProvider.isScreenOn))
            }
            .subscribe { updateNavBarState(it) }
            .disposeBy(enabledScope)

        // force show on shut downs
        broadcastFactory.create(Intent.ACTION_SHUTDOWN)
            .subscribe {
                enabledScope.clear()
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

            val navBarHeight = getNavigationBarHeight()
            val rect = getOverscanRect(if (hide) -navBarHeight else 0)
            overscanHelper.setDisplayOverscan(rect)
            prefs.wasNavBarHidden.set(hide)
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
        return if (id > 0) {
            app.resources.getDimensionPixelSize(id)
        } else 0
    }

    private fun getOverscanRect(navBarHeight: Int) = when (prefs.rotationMode.get()) {
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

    private fun prefChanges(): Observable<Unit> {
        return Observables.combineLatest(
            prefs.navBarHidden.asObservable(),
            prefs.rotationMode.asObservable(),
            prefs.showNavBarScreenOff.asObservable()
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
                    Observable.empty()
                }
            }
            .map { Unit }
    }

    private fun rotationChanges() = observable<Int> {
        var currentRotation = displayRotationProvider.displayRotation

        val listener = object : OrientationEventListener(
            app, SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = displayRotationProvider.displayRotation
                if (rotation != currentRotation) {
                    onNext(rotation)
                    currentRotation = rotation
                }
            }

        }

        setCancellable { listener.disable() }

        listener.enable()

        onNext(currentRotation)
    }

    private fun configChanges() = observable<Unit> {
        val callbacks = app.doOnConfigurationChanged { onNext(Unit) }
        setCancellable { app.unregisterComponentCallbacks(callbacks) }
    }

    private fun screenState(): Observable<Boolean> {
        return broadcastFactory.create(
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_USER_PRESENT
        )
            .map { screenStateProvider.isScreenOff }
            .startWith(screenStateProvider.isScreenOff)
    }

}