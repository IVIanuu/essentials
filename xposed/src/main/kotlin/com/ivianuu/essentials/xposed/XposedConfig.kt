/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import android.content.pm.*
import com.ivianuu.injekt.*
import de.robv.android.xposed.callbacks.*

data class XposedConfig(
  val packageName: String,
  val processName: String,
  @property:Provide val classLoader: ClassLoader,
  val appInfo: ApplicationInfo,
  val modulePackageName: String,
) {
  @Provide companion object {
    @Provide fun default(
      params: XC_LoadPackage.LoadPackageParam,
      modulePackageName: ModulePackageName,
    ) = XposedConfig(
      packageName = params.packageName,
      processName = params.processName,
      classLoader = params.classLoader,
      appInfo = params.appInfo,
      modulePackageName = modulePackageName.value
    )
  }
}
