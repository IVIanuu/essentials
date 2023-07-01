/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import android.content.pm.ApplicationInfo
import com.ivianuu.injekt.Provide
import de.robv.android.xposed.callbacks.XC_LoadPackage

data class XposedConfig(
  val packageName: String,
  val processName: String,
  @Provide val classLoader: ClassLoader,
  val appInfo: ApplicationInfo,
  val modulePackageName: String,
) {
  companion object {
    @Provide fun default(
      lpparam: XC_LoadPackage.LoadPackageParam,
      modulePackageName: ModulePackageName,
    ): XposedConfig = XposedConfig(
      packageName = lpparam.packageName,
      processName = lpparam.processName,
      classLoader = lpparam.classLoader,
      appInfo = lpparam.appInfo,
      modulePackageName = modulePackageName.value
    )
  }
}
