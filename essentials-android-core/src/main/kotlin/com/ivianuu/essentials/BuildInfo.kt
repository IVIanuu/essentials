/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped

data class BuildInfo(
  val isDebug: Boolean,
  val appName: String,
  val packageName: String,
  val versionName: String,
  val versionCode: Int,
) {
  companion object {
    @Provide @Scoped<AppComponent>
    fun androidBuildInfo(context: AppContext, packageManager: PackageManager): BuildInfo {
      val appInfo = context.applicationInfo
      val packageInfo = packageManager
        .getPackageInfo(appInfo.packageName, 0)
      return BuildInfo(
        isDebug = appInfo.flags.hasFlag(ApplicationInfo.FLAG_DEBUGGABLE),
        appName = appInfo.loadLabel(packageManager).toString(),
        packageName = appInfo.packageName,
        versionName = packageInfo.versionName,
        versionCode = packageInfo.versionCode
      )
    }
  }
}
