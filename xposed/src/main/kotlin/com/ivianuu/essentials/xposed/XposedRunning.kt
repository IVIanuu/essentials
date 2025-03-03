/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import injekt.*

@JvmInline value class IsXposedRunning(val value: Boolean) {
  @Provide companion object {
    @Provide val default get() = IsXposedRunning(isXposedRunning())
  }
}

private fun isXposedRunning() = false

@Provide fun xposedRunningHooks() = Hooks { config ->
  if (config.packageName == config.modulePackageName)
    hookAllMethods(
      "com.ivianuu.essentials.xposed.XposedRunningKt",
      "isXposedRunning"
    ) {
      replace { true }
    }
}

fun main() {
}