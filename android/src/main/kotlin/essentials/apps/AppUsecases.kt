/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.apps

import android.content.pm.*
import androidx.compose.ui.util.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Tag typealias InstalledApps = List<AppInfo>

@Provide suspend fun getInstalledApps(
  coroutineContexts: CoroutineContexts,
  packageManager: PackageManager
): InstalledApps = withContext(coroutineContexts.io) {
  packageManager.getInstalledApplications(0)
    .parMap {
      AppInfo(
        appName = it.loadLabel(packageManager).toString(),
        packageName = it.packageName
      )
    }
    .fastDistinctBy { it.packageName }
    .sortedBy { it.appName.lowercase() }
}

@Provide suspend fun getAppInfo(
  packageName: String,
  coroutineContexts: CoroutineContexts,
  packageManager: PackageManager
): AppInfo? = withContext(coroutineContexts.io) {
  val applicationInfo = catch {
    packageManager.getApplicationInfo(packageName, 0)
  }.getOrNull() ?: return@withContext null
  AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
}

data class AppInfo(val packageName: String, val appName: String)
