/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Stable
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ui.animation.transition.FadeScaleStackTransition
import com.ivianuu.injekt.Provide

@Stable interface Screen<T>

interface RootScreen : Screen<Unit>

interface OverlayScreen<T> : Screen<T> {
  companion object {
    @Provide fun <T : OverlayScreen<*>> overlayScreenConfig() =
      ScreenConfig<T>(opaque = true, transition = FadeScaleStackTransition())
  }
}

interface CriticalUserFlowScreen<T> : Screen<T>

val Scope<*>.screen: Screen<*> get() = service()