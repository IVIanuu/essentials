/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.Provide

fun interface KeyHandler<R> : suspend (Key<R>) -> (suspend () -> R?)? {
  companion object {
    @Provide fun defaultKeyHandlers() = emptyList<KeyHandler<*>>()
  }
}
