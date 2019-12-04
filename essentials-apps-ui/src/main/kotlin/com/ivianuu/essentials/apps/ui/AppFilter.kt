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

package com.ivianuu.essentials.apps.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.Composable
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.injekt.inject

typealias AppFilter = (AppInfo) -> Boolean

val DefaultAppFilter: AppFilter = { true }

class CachingAppFilter(private val appFilter: AppFilter) : AppFilter {
    private val cachedResults = mutableMapOf<String, Boolean>()
    override fun invoke(app: AppInfo): Boolean = cachedResults.getOrPut(app.packageName) {
        appFilter(app)
    }
}

@Composable
fun launchableOnlyAppFilter(): AppFilter = effect {
    val packageManager = inject<PackageManager>()
    return@effect CachingAppFilter { app ->
        packageManager.getLaunchIntentForPackage(app.packageName) != null
    }
}

@Composable
fun intentAppFilter(intent: Intent): AppFilter = effect {
    val packageManager = inject<PackageManager>()
    val mediaApps by lazy {
        packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo.packageName }
    }
    return@effect CachingAppFilter { it.packageName in mediaApps }
}
