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

@Provide class LaunchableAppPredicate(private val packageManager: PackageManager): AppPredicate {
  val cache = mutableMapOf<String, Boolean>()
  override fun invoke(app: AppInfo): Boolean = cache.getOrPut(app.packageName) {
    packageManager.getLaunchIntentForPackage(app.packageName) != null
  }
}

@Provide class IntentAppPredicate(
  private val intent: Intent,
  private val packageManager: PackageManager
) : AppPredicate {
  private val apps by lazy {
    packageManager.queryIntentActivities(intent, 0)
      .map { it.activityInfo.applicationInfo.packageName }
  }
  override fun invoke(app: AppInfo): Boolean = app.packageName in apps
}
