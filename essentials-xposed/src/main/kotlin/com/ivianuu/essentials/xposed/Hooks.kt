/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

fun interface Hooks {
  operator fun XposedContext.invoke()

  companion object {
    @Provide val defaultHooks: Collection<Hooks> get() = emptyList()
  }
}
