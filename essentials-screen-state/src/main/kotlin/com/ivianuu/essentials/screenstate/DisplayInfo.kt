/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.util.DisplayMetrics
import android.view.WindowManager
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

data class DisplayInfo(
  val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
  val screenWidth: Int = 0,
  val screenHeight: Int = 0
) {
  @JvmInline value class Provider(val displayInfo: Flow<DisplayInfo>)
}

context(ConfigChangeProvider, DisplayRotation.Provider, WindowManager)
    @Provide fun displayInfoProvider() = DisplayInfo.Provider(
  flow {
    combine(
      configChanges
        .onStart { emit(Unit) },
      displayRotation
    ) { _, rotation ->
      val metrics = DisplayMetrics()
      defaultDisplay.getRealMetrics(metrics)
      DisplayInfo(
        rotation = rotation,
        screenWidth = metrics.widthPixels,
        screenHeight = metrics.heightPixels
      )
    }
      .distinctUntilChanged()
      .let { emitAll(it) }
  }
)
