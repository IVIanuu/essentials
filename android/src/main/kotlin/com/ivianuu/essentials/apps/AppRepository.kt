/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import android.content.Intent
import android.content.pm.PackageManager
import arrow.core.Either
import arrow.fx.coroutines.parMap
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Provide class AppRepository(
  private val broadcastsFactory: BroadcastsFactory,
  private val coroutineContexts: CoroutineContexts,
  private val packageManager: PackageManager
) {
  val installedApps: Flow<List<AppInfo>> = flow {
    merge(
      broadcastsFactory(Intent.ACTION_PACKAGE_ADDED),
      broadcastsFactory(Intent.ACTION_PACKAGE_REMOVED),
      broadcastsFactory(Intent.ACTION_PACKAGE_CHANGED),
      broadcastsFactory(Intent.ACTION_PACKAGE_REPLACED)
    )
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
      .let { emitAll(it) }
  }

  fun appInfo(packageName: String) = broadcastsFactory(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED,
    Intent.ACTION_PACKAGE_CHANGED,
    Intent.ACTION_PACKAGE_REPLACED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(coroutineContexts.io) {
        val applicationInfo = Either.catch {
          packageManager.getApplicationInfo(packageName, 0)
        }.getOrNull() ?: return@withContext null
        AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
      }
    }
    .distinctUntilChanged()
}

data class AppInfo(val packageName: String, val appName: String)
