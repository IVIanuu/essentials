/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.fold
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IOContext
import com.ivianuu.injekt.inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface AppRepository {
  val installedApps: Flow<List<AppInfo>>

  fun appInfo(packageName: String): Flow<AppInfo?>

  fun isAppInstalled(packageName: String): Flow<Boolean>
}

@Provide class AppRepositoryImpl(
  private val broadcastsFactory: BroadcastsFactory,
  private val context: IOContext,
  private val packageManager: PackageManager
) : AppRepository {
  override val installedApps: Flow<List<AppInfo>>
    get() = merge(
      broadcastsFactory(Intent.ACTION_PACKAGE_ADDED),
      broadcastsFactory(Intent.ACTION_PACKAGE_REMOVED),
      broadcastsFactory(Intent.ACTION_PACKAGE_CHANGED),
      broadcastsFactory(Intent.ACTION_PACKAGE_REPLACED)
    )
      .onStart<Any?> { emit(Unit) }
      .map {
        withContext(context) {
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

  override fun appInfo(packageName: String) = broadcastsFactory(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED,
    Intent.ACTION_PACKAGE_CHANGED,
    Intent.ACTION_PACKAGE_REPLACED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(context) {
        val applicationInfo = catch {
          packageManager.getApplicationInfo(packageName, 0)
        }.getOrNull() ?: return@withContext null
        AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
      }
    }
    .distinctUntilChanged()

  override fun isAppInstalled(packageName: String) = broadcastsFactory(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(context) {
        catch { packageManager.getApplicationInfo(packageName, 0) }
          .fold(success = { true }, failure = { false })
      }
    }
    .distinctUntilChanged()
}

data class AppInfo(val packageName: String, val appName: String)
