package com.ivianuu.essentials.hidenavbar

import android.app.Application
import android.hardware.SensorManager
import android.view.OrientationEventListener
import android.view.WindowManager
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.observable
import com.ivianuu.injekt.Inject
import com.ivianuu.kommon.core.app.doOnConfigurationChanged
import io.reactivex.Observable

@Inject
internal class DisplayRotationProvider(
    private val app: Application,
    private val screenStateProvider: ScreenStateProvider,
    private val windowManager: WindowManager
) {

    val displayRotation: Int get() = windowManager.defaultDisplay.rotation

    fun observeRotationChanges(): Observable<Int> {
        return screenStateProvider.observeScreenStateChanges()
            .switchMap {
                if (it) {
                    Observable.merge(listOf(rotationChanges(), configChanges()))
                        .doOnSubscribe { d { "sub for rotation" } }
                        .doOnDispose { d { "dispose rotation" } }
                } else {
                    d { "do not observe rotation while screen is off" }
                    Observable.empty()
                }
            }
            .map { displayRotation }
            .startWith(displayRotation)
    }

    private fun rotationChanges() = observable<Int> {
        var currentRotation = displayRotation

        val listener = object : OrientationEventListener(
            app, SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = displayRotation
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

}