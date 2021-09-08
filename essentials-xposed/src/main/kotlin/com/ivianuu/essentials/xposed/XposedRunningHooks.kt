package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

@Provide val xposedRunningHooks: Hooks = {
  hookAllMethods(
    "com.ivianuu.essentials.xposed.XposedRunningUtilKt",
    "isXposedRunning"
  ) {
    replace { true }
  }
}
