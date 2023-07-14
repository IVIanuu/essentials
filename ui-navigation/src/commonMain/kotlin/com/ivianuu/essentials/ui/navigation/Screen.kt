/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Stable
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ui.animation.ContentKey
import com.ivianuu.injekt.Provide

@Stable interface Screen<T>

interface RootScreen : Screen<Unit>

interface OverlayScreen<T> : Screen<T> {
  @Provide companion object {
    @Provide fun <T : OverlayScreen<*>> screenConfig() =
      ScreenConfig<T>(opaque = true, transitionSpec = {
        if (isPush)
          ContentKey entersWith fadeIn()
        else
          ContentKey exitsWith fadeOut()
      })
  }
}

interface CriticalUserFlowScreen<T> : Screen<T>

val Scope<*>.screen: Screen<*> get() = service()
