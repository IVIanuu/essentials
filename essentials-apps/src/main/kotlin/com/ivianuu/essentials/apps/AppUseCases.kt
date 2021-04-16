/*
 * Copyright 2020 Manuel Wrage
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

import android.content.pm.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

typealias GetInstalledAppsUseCase = suspend () -> List<AppInfo>

@Given
fun getInstalledAppsUseCase(
    @Given dispatcher: IODispatcher,
    @Given packageManager: PackageManager
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

@Given
fun getAppInfoUseCase(
    @Given dispatcher: IODispatcher,
    @Given packageManager: PackageManager
): GetAppInfoUseCase = { packageName ->
    withContext(dispatcher) {
        val applicationInfo = runCatching {
            packageManager.getApplicationInfo(packageName, 0)
        }.get() ?: return@withContext null
        AppInfo(packageName, applicationInfo.loadLabel(packageManager).toString())
    }
}

data class AppInfo(val packageName: String, val appName: String)
