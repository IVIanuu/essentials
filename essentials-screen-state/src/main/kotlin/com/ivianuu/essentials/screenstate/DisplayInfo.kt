/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.util.*
import android.view.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.flow.*

data class DisplayInfo(
  val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
  val screenWidth: Int = 0,
  val screenHeight: Int = 0
)

@Provide fun displayInfo(
  configChanges: () -> Flow<ConfigChange>,
  displayRotation: () -> Flow<DisplayRotation>,
  windowManager: @SystemService WindowManager
): Flow<DisplayInfo> = flow {
  combine(
    configChanges()
      .onStart { emit(ConfigChange) },
    displayRotation()
  ) { _, rotation ->
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(metrics)
    DisplayInfo(
      rotation = rotation,
      screenWidth = metrics.widthPixels,
      screenHeight = metrics.heightPixels
    )
  }
    .distinctUntilChanged()
    .let { emitAll(it) }
}

