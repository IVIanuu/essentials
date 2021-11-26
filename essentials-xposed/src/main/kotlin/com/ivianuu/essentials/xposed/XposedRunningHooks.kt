package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

@Provide fun xposedRunningHooks(modulePackageName: ModulePackageName) = Hooks {
  if (it.packageName.value == modulePackageName.value) {
    hookAllMethods(
      "com.ivianuu.essentials.xposed.XposedRunningUtilKt",
      "isXposedRunning"
    ) {
      replace { true }
    }
  }
}
