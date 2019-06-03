/*
 * Copyright 2018 Manuel Wrage
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
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Factory

import kotlinx.coroutines.withContext

/**
 * Store for [AppInfo]s
 */
@Factory
class AppStore(
    private val dispatchers: AppDispatchers,
    private val packageManager: PackageManager
) {

    suspend fun getInstalledApps(): List<AppInfo> = withContext(dispatchers.io) {
        packageManager.getInstalledApplications(0)
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.toLowerCase() }
            .toList()
    }

    suspend fun getLaunchableApps(): List<AppInfo> = withContext(dispatchers.io) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        packageManager.queryIntentActivities(intent, 0)
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.toLowerCase() }
            .toList()
    }

    suspend fun getAppInfo(packageName: String): AppInfo = withContext(dispatchers.io) {
        AppInfo(
            packageName,
            packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                .toString()
        )
    }
}