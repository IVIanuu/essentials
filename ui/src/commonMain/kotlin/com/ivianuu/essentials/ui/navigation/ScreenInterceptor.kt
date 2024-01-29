/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.Provide

fun interface ScreenInterceptor<R> {
  suspend operator fun invoke(screen: Screen<R>): (suspend () -> R?)?

  @Provide companion object {
    @Provide val defaultScreenInterceptors get() = emptyList<ScreenInterceptor<*>>()
  }
}
