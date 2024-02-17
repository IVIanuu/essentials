/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.injekt.*

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
