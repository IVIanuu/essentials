/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Stable
import com.ivianuu.essentials.ui.animation.transition.FadeScaleStackTransition
import com.ivianuu.injekt.Provide

@Stable interface Key<T>

interface RootKey : Key<Unit>

interface OverlayKey<T> : Key<T> {
  companion object {
    @Provide fun <T : OverlayKey<*>> overlayKeyUiOptionsFactory() =
      KeyUiOptionsFactory<T> { _, _, _ ->
        KeyUiOptions(opaque = true, transition = FadeScaleStackTransition())
      }
  }
}

interface CriticalUserFlowKey<T> : Key<T>
