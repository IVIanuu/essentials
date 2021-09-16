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
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.get
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

typealias GetInstalledAppsUseCase = suspend () -> List<AppInfo>

@Provide fun getInstalledAppsUseCase(
  dispatcher: IODispatcher,
  packageManager: PackageManager
): GetInstalledAppsUseCase = {
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

typealias GetAppInfoUseCase = suspend (String) -> AppInfo?

@Provide fun getAppInfoUseCase(
  dispatcher: IODispatcher,
  packageManager: PackageManager
): GetAppInfoUseCase = { packageName ->
  withContext(dispatcher) {
    val applicationInfo = catch {
      packageManager.getApplicationInfo(packageName, 0)
    }.get() ?: return@withContext null
    AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
  }
}

data class AppInfo(val packageName: String, val appName: String)

typealias IsAppInstalled = Boolean

typealias PackageName = String

@Provide fun isAppInstalled(
  broadcastsFactory: BroadcastsFactory,
  dispatcher: IODispatcher,
  packageManager: PackageManager,
  packageName: PackageName
): Flow<IsAppInstalled> = merge(
  broadcastsFactory(Intent.ACTION_PACKAGE_ADDED),
  broadcastsFactory(Intent.ACTION_PACKAGE_REMOVED)
)
  .map { Unit }
  .onStart { emit(Unit) }
  .map {
    withContext(dispatcher) {
      catch { packageManager.getApplicationInfo(packageName, 0) }
        .fold(success = { true }, failure = { false })
    }
  }
  .distinctUntilChanged()
