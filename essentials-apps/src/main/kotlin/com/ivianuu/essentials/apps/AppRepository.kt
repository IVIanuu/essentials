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

context(BroadcastsFactory, PackageManager) @Provide class AppRepositoryImpl(
  private val context: IOContext
) : AppRepository {
  override val installedApps: Flow<List<AppInfo>>
    get() = merge(
      broadcasts(Intent.ACTION_PACKAGE_ADDED),
      broadcasts(Intent.ACTION_PACKAGE_REMOVED),
      broadcasts(Intent.ACTION_PACKAGE_CHANGED),
      broadcasts(Intent.ACTION_PACKAGE_REPLACED)
    )
      .onStart<Any?> { emit(Unit) }
      .map {
        withContext(context) {
          getInstalledApplications(0)
            .parMap {
              AppInfo(
                appName = it.loadLabel(inject()).toString(),
                packageName = it.packageName
              )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.toLowerCase() }
            .toList()
        }
      }
      .distinctUntilChanged()

  override fun appInfo(packageName: String) = broadcasts(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED,
    Intent.ACTION_PACKAGE_CHANGED,
    Intent.ACTION_PACKAGE_REPLACED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(context) {
        val applicationInfo = catch {
          getApplicationInfo(packageName, 0)
        }.getOrNull() ?: return@withContext null
        AppInfo(packageName, applicationInfo.loadLabel(inject()).toString())
      }
    }
    .distinctUntilChanged()

  override fun isAppInstalled(packageName: String) = broadcasts(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(context) {
        catch { getApplicationInfo(packageName, 0) }
          .fold(success = { true }, failure = { false })
      }
    }
    .distinctUntilChanged()
}

data class AppInfo(val packageName: String, val appName: String)
