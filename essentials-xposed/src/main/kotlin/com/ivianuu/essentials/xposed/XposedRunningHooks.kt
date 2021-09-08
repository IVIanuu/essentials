package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

@Provide fun xposedRunningHooks(modulePackageName: ModulePackageName): Hooks = {
  if (packageName == modulePackageName) {
    hookAllMethods(
      "com.ivianuu.essentials.xposed.XposedRunningUtilKt",
      "isXposedRunning"
    ) {
      replace { true }
    }
  }
}
