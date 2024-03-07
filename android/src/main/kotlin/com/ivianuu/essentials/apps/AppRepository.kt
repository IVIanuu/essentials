/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import android.content.*
import android.content.pm.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide class AppRepository(
  private val broadcastManager: BroadcastManager,
  private val coroutineContexts: CoroutineContexts,
  private val packageManager: PackageManager
) {
  val installedApps: Flow<List<AppInfo>> = appChanges()
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(coroutineContexts.io) {
        packageManager.getInstalledApplications(0)
          .parMap {
            AppInfo(
              appName = it.loadLabel(packageManager).toString(),
              packageName = it.packageName
            )
          }
          .distinctBy { it.packageName }
          .sortedBy { it.appName.toLowerCase() }
          .toList()
      }
    }
    .distinctUntilChanged()

  fun appInfo(packageName: String): Flow<AppInfo?> = appChanges()
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(coroutineContexts.io) {
        val applicationInfo = catch {
          packageManager.getApplicationInfo(packageName, 0)
        }.getOrNull() ?: return@withContext null
        AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
      }
    }
    .distinctUntilChanged()

  private fun appChanges() = merge(
    broadcastManager.broadcasts(Intent.ACTION_PACKAGE_ADDED),
    broadcastManager.broadcasts(Intent.ACTION_PACKAGE_REMOVED),
    broadcastManager.broadcasts(Intent.ACTION_PACKAGE_CHANGED),
    broadcastManager.broadcasts(Intent.ACTION_PACKAGE_REPLACED)
  )
}

data class AppInfo(val packageName: String, val appName: String)
