/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

object RotationChange

@Provide fun rotationChanges(
  context: AppContext,
  coroutineContext: MainContext,
): Flow<RotationChange> = callbackFlow<RotationChange> {
  val listener = object :
    OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
    override fun onOrientationChanged(orientation: Int) {
      trySend(RotationChange)
    }
  }
  listener.enable()
  awaitClose { listener.disable() }
}.flowOn(coroutineContext)
