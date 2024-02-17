/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import android.content.*
import android.content.pm.*
import com.ivianuu.injekt.*

fun interface AppPredicate {
  operator fun invoke(app: AppInfo): Boolean
}

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
  intent: Intent,
  packageManager: PackageManager
): IntentAppPredicate {
  val apps by lazy {
    packageManager.queryIntentActivities(intent, 0)
      .map { it.activityInfo.applicationInfo.packageName }
  }
  return IntentAppPredicate { app -> app.packageName in apps }
}
