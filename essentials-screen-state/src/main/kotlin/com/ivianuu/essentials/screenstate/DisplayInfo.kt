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

import android.util.*
import android.view.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*

data class DisplayInfo(
  val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
  val screenWidth: Int = 0,
  val screenHeight: Int = 0
)

@Provide fun displayInfo(
  configChanges: () -> Flow<ConfigChange>,
  displayRotation: () -> Flow<DisplayRotation>,
  scope: InjektCoroutineScope<AppScope>,
  windowManager: @SystemService WindowManager
): @Scoped<AppScope> Flow<DisplayInfo> = flow {
  combine(
    configChanges()
      .onStart { emit(Unit) },
    displayRotation()
  ) { _, rotation ->
    metricsMutex.withLock {
      windowManager.defaultDisplay.getRealMetrics(metrics)
      DisplayInfo(
        rotation = rotation,
        screenWidth = metrics.widthPixels,
        screenHeight = metrics.heightPixels
      )
    }
  }.let { emitAll(it) }
}
  .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
  .distinctUntilChanged()

private val metrics = DisplayMetrics()
private val metricsMutex = Mutex()
