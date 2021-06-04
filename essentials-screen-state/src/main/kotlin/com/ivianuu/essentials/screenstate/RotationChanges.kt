package com.ivianuu.essentials.screenstate

import android.hardware.*
import android.view.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*


typealias RotationChange = Unit

@Provide fun rotationChanges(
  context: AppContext,
  mainDispatcher: MainDispatcher,
): Flow<RotationChange> = callbackFlow<RotationChange> {
  val listener = object :
    OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
    override fun onOrientationChanged(orientation: Int) {
      trySend(RotationChange)
    }
  }
  listener.enable()
  awaitClose { listener.disable() }
}.flowOn(mainDispatcher)
