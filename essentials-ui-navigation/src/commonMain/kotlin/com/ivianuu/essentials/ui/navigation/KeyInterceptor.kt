/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.Provide

fun interface KeyInterceptor<R> {
  suspend operator fun invoke(key: Key<R>): (suspend () -> R?)?

  companion object {
    @Provide val defaultKeyInterceptors get() = emptyList<KeyInterceptor<*>>()
  }
}
