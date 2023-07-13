/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

fun interface Hooks {
  context(XposedConfig) operator fun invoke()

  companion object {
    @Provide val defaultHooks get() = emptyList<Hooks>()
  }
}
