/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import android.app.*
import android.content.pm.*
import android.os.*
import injekt.*

data class AppConfig(
  val isDebug: Boolean,
  val appName: String,
  val packageName: String,
  val versionName: String,
  val versionCode: Int,
  val sdk: Int,
  val deviceModel: String,
  val deviceManufacturer: String
) {
  @Provide companion object {
    @Provide fun androidAppConfig(context: Application): @Scoped<AppScope> AppConfig {
      val appInfo = context.applicationInfo
      val packageInfo = context.packageManager
        .getPackageInfo(appInfo.packageName, 0)
      return AppConfig(
        isDebug = appInfo.flags.hasFlag(ApplicationInfo.FLAG_DEBUGGABLE),
        appName = appInfo.loadLabel(context.packageManager).toString(),
        packageName = appInfo.packageName,
        versionName = packageInfo.versionName!!,
        versionCode = packageInfo.versionCode,
        sdk = Build.VERSION.SDK_INT,
        deviceModel = Build.MODEL,
        deviceManufacturer = Build.MANUFACTURER
      )
    }
  }
}
