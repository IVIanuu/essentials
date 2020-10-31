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
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun getInstalledApps(
    ioDispatcher: IODispatcher,
    packageManager: PackageManager,
): List<AppInfo> = withContext(ioDispatcher) {
    packageManager.getInstalledApplications(0)
        .parallelMap {
            AppInfo(
                appName = it.loadLabel(packageManager).toString(),
                packageName = it.packageName
            )
        }
        .distinctBy { it.packageName }
        .sortedBy { it.appName.toLowerCase() }
        .toList()
}

@FunBinding
suspend fun getAppInfo(
    ioDispatcher: IODispatcher,
    packageManager: PackageManager,
    @FunApi packageName: String
): AppInfo = withContext(ioDispatcher) {
    AppInfo(
        packageName,
        packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
            .toString()
    )
}
