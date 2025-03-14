/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.apps

import android.content.*
import androidx.compose.ui.util.*
import essentials.*
import injekt.*

fun interface AppPredicate {
  fun test(app: AppInfo): Boolean
}

val DefaultAppPredicate = AppPredicate { true }

fun launchableAppPredicate(scope: Scope<*> = inject): AppPredicate {
  val cache = mutableMapOf<String, Boolean>()
  return AppPredicate {
    cache.getOrPut(it.packageName) {
      packageManager().getLaunchIntentForPackage(it.packageName) != null
    }
  }
}

fun intentAppPredicate(intent: Intent, scope: Scope<*> = inject): AppPredicate {
  val apps by lazy {
    packageManager().queryIntentActivities(intent, 0)
      .fastMap { it.activityInfo.applicationInfo.packageName }
  }
  return AppPredicate { it.packageName in apps }
}
