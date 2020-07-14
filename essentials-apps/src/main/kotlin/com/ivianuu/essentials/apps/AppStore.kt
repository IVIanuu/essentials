/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext

@Reader
suspend fun getInstalledApps(): List<AppInfo> = withContext(dispatchers.io) {
    val pm = given<PackageManager>()
    pm.getInstalledApplications(0)
        .parallelMap {
            AppInfo(
                appName = it.loadLabel(pm).toString(),
                packageName = it.packageName
            )
        }
        .distinctBy { it.packageName }
        .sortedBy { it.appName.toLowerCase() }
        .toList()
}

@Reader
suspend fun getAppInfo(packageName: String): AppInfo = withContext(dispatchers.io) {
    val pm = given<PackageManager>()
    AppInfo(
        packageName,
        pm.getApplicationInfo(packageName, 0).loadLabel(pm)
            .toString()
    )
}
