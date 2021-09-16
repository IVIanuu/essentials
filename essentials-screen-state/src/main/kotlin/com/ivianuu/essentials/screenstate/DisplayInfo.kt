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

import android.util.DisplayMetrics
import android.view.WindowManager
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class DisplayInfo(
  val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
  val screenWidth: Int = 0,
  val screenHeight: Int = 0
)

@Provide fun displayInfo(
  configChanges: () -> Flow<ConfigChange>,
  displayRotation: () -> Flow<DisplayRotation>,
  scope: NamedCoroutineScope<AppScope>,
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
