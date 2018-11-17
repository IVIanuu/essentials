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

package com.ivianuu.essentials.app

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.util.ext.coroutinesIo
import com.ivianuu.timberktx.d
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Store for [AppInfo]s
 */
class AppStore @Inject constructor(private val packageManager: PackageManager) {

    suspend fun installedApps() = withContext(coroutinesIo) {
        d { "running on ${Thread.currentThread().name}" }
        packageManager.getInstalledApplications(0)
            .asSequence()
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

    suspend fun launchableApps() = withContext(coroutinesIo) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        packageManager.queryIntentActivities(intent, 0)
            .asSequence()
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

    suspend fun appInfo(packageName: String) = withContext(coroutinesIo) {
        AppInfo(
            packageName,
            packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                .toString()
        )
    }
}