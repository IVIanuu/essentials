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

package com.ivianuu.essentials.apps.ui

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.injekt.Provide

fun interface AppPredicate : (AppInfo) -> Boolean

val DefaultAppPredicate = AppPredicate { true }

fun interface LaunchableAppPredicate : AppPredicate

@Provide fun launchableAppPredicate(packageManager: PackageManager): LaunchableAppPredicate {
  val cache = mutableMapOf<String, Boolean>()
  return LaunchableAppPredicate { app ->
    cache.getOrPut(app.packageName) {
      packageManager.getLaunchIntentForPackage(app.packageName) != null
    }
  }
}

fun interface IntentAppPredicate : AppPredicate

@Provide fun intentAppPredicate(
  packageManager: PackageManager,
  intent: Intent
): IntentAppPredicate {
  val apps by lazy {
    packageManager.queryIntentActivities(intent, 0)
      .map { it.activityInfo.applicationInfo.packageName }
  }
  return IntentAppPredicate { app -> app.packageName in apps }
}
