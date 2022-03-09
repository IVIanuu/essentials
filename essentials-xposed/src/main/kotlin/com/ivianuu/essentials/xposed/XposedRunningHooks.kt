/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed


@Provide fun xposedRunningHooks(modulePackageName: ModulePackageName) = Hooks {
  if (packageName.value == modulePackageName.value) {
    hookAllMethods(
      "com.ivianuu.essentials.xposed.XposedRunningUtilKt",
      "isXposedRunning"
    ) {
      replace { true }
    }
  }
}
