/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.hardware.*
import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

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
