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

package com.ivianuu.essentials.apps.ui

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding

typealias AppFilter = (AppInfo) -> Boolean

val DefaultAppFilter: AppFilter = { true }

@Binding
class CachingAppFilter(private val appFilter: AppFilter) : @Assisted AppFilter {
    private val cachedResults = mutableMapOf<String, Boolean>()
    override fun invoke(app: AppInfo): Boolean = cachedResults.getOrPut(app.packageName) {
        appFilter(app)
    }
}

@Binding
class LaunchableAppFilter(
    private val packageManager: PackageManager,
) : AppFilter {
    private val wrapped = CachingAppFilter { app ->
        packageManager.getLaunchIntentForPackage(app.packageName) != null
    }

    override fun invoke(app: AppInfo) = wrapped(app)
}

@Binding
class IntentAppFilter(
    private val packageManager: PackageManager,
    private val intent: @Assisted Intent,
) : AppFilter {
    private val apps by lazy {
        packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo.packageName }
    }

    private val wrapped = CachingAppFilter { app ->
        app.packageName in apps
    }

    override fun invoke(app: AppInfo) = wrapped(app)
}
