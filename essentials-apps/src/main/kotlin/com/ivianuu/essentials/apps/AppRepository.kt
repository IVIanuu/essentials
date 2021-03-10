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

import android.content.pm.PackageManager
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.injekt.Given
import kotlinx.coroutines.withContext

interface AppRepository {

    suspend fun getInstalledApps(): List<AppInfo>

    suspend fun getAppInfo(packageName: String): AppInfo

}

@Given
class AppRepositoryImpl(
    @Given private val ioDispatcher: IODispatcher,
    @Given private val packageManager: PackageManager
) : AppRepository {

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(ioDispatcher) {
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

    override suspend fun getAppInfo(packageName: String): AppInfo = withContext(ioDispatcher) {
        AppInfo(
            packageName,
            packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                .toString()
        )
    }

}
