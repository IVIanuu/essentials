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
  coroutineContexts: CoroutineContexts
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
  coroutineContexts: CoroutineContexts,
  context: Application = inject
): AppInfo? = withContext(coroutineContexts.io) {
  val applicationInfo = catch {
    context.packageManager.getApplicationInfo(this@toAppInfo, 0)
  }.getOrNull() ?: return@withContext null
  AppInfo(this@toAppInfo, applicationInfo.loadLabel(context.packageManager).toString())
}

data class AppInfo(val packageName: String, val appName: String)
