/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.content.pm.ApplicationInfo
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped

data class BuildInfo(
  val isDebug: Boolean,
  val appName: String,
  val packageName: String,
  val versionName: String,
  val versionCode: Int,
) {
  companion object {
    @Provide fun androidBuildInfo(appContext: AppContext): @Scoped<AppScope> BuildInfo {
      val appInfo = appContext.applicationInfo
      val packageInfo = appContext.packageManager
        .getPackageInfo(appInfo.packageName, 0)
      return BuildInfo(
        isDebug = appInfo.flags.hasFlag(ApplicationInfo.FLAG_DEBUGGABLE),
        appName = appInfo.loadLabel(appContext.packageManager).toString(),
        packageName = appInfo.packageName,
        versionName = packageInfo.versionName,
        versionCode = packageInfo.versionCode
      )
    }
  }
}
