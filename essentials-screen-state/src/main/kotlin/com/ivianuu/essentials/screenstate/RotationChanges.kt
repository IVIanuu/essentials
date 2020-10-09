package com.ivianuu.essentials.screenstate

import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@FunBinding
fun rotationChanges(applicationContext: ApplicationContext): Flow<Unit> = callbackFlow {
    val listener = object :
        OrientationEventListener(applicationContext, SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
            offerSafe(Unit)
        }
    }
    listener.enable()
    awaitClose { listener.disable() }
}
