/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.screenstate

import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

object RotationChange

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
