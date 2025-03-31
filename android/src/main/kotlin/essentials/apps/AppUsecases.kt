/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.apps

import android.app.*
import androidx.compose.ui.util.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Tag typealias getInstalledApps = suspend () -> List<AppInfo>

@Provide fun getInstalledApps(
  context: Application = inject,
  coroutineContexts: CoroutineContexts = inject
): getInstalledApps = {
  withContext(coroutineContexts.io) {
    context.packageManager.getInstalledApplications(0)
      .parMap {
        AppInfo(
          appName = it.loadLabel(context.packageManager).toString(),
          packageName = it.packageName
        )
      }
      .fastDistinctBy { it.packageName }
      .sortedBy { it.appName.lowercase() }
  }
}

@Provide suspend fun String.toAppInfo(
  context: Application = inject,
  coroutineContexts: CoroutineContexts = inject
): AppInfo? = withContext(coroutineContexts.io) {
  catch { context.packageManager.getApplicationInfo(this@toAppInfo, 0) }
    .catchMap { AppInfo(this@toAppInfo, it.loadLabel(context.packageManager).toString()) }
    .getOrNull()
}

data class AppInfo(val packageName: String, val appName: String)
