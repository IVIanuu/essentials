/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.apps

import android.content.*
import androidx.compose.ui.util.*
import injekt.*

fun interface AppPredicate {
  fun test(app: AppInfo): Boolean
}

val DefaultAppPredicate = AppPredicate { true }

fun launchableAppPredicate(context: Context = inject): AppPredicate {
  val cache = mutableMapOf<String, Boolean>()
  return AppPredicate { app ->
    cache.getOrPut(app.packageName) {
      context.packageManager.getLaunchIntentForPackage(app.packageName) != null
    }
  }
}

fun Intent.asAppPredicate(context: Context = inject): AppPredicate {
  val apps by lazy {
    context.packageManager.queryIntentActivities(this, 0)
      .fastMap { it.activityInfo.applicationInfo.packageName }
  }
  return AppPredicate { it.packageName in apps }
}
