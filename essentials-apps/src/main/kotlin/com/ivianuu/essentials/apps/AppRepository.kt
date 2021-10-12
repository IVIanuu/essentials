/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Provide class AppRepository(
  private val broadcastsFactory: BroadcastsFactory,
  private val dispatcher: IODispatcher,
  private val packageManager: PackageManager
) {
  val installedApps: Flow<List<AppInfo>>
    get() = merge(
      broadcastsFactory(Intent.ACTION_PACKAGE_ADDED),
      broadcastsFactory(Intent.ACTION_PACKAGE_REMOVED),
      broadcastsFactory(Intent.ACTION_PACKAGE_CHANGED),
      broadcastsFactory(Intent.ACTION_PACKAGE_REPLACED)
    )
      .onStart<Any?> { emit(Unit) }
      .map {
        withContext(dispatcher) {
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

  fun appInfo(packageName: String): Flow<AppInfo?> = broadcastsFactory(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED,
    Intent.ACTION_PACKAGE_CHANGED,
    Intent.ACTION_PACKAGE_REPLACED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(dispatcher) {
        val applicationInfo = catch {
          packageManager.getApplicationInfo(packageName, 0)
        }.getOrNull() ?: return@withContext null
        AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
      }
    }
    .distinctUntilChanged()

  fun isAppInstalled(packageName: String): Flow<Boolean> = broadcastsFactory(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(dispatcher) {
        catch { packageManager.getApplicationInfo(packageName, 0) }
          .fold(success = { true }, failure = { false })
      }
    }
    .distinctUntilChanged()
}

data class AppInfo(val packageName: String, val appName: String)
