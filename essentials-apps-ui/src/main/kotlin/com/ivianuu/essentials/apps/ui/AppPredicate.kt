/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui

import android.content.*
import android.content.pm.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.injekt.*

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
