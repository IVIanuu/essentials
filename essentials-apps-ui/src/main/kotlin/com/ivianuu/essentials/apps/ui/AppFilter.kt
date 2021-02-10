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
import com.ivianuu.essentials.memo.memoize
import com.ivianuu.injekt.Given

typealias AppFilter = (AppInfo) -> Boolean

val DefaultAppFilter: AppFilter = { true }

typealias LaunchableAppFilter = AppFilter
@Given
fun LaunchableAppFilter(
    @Given packageManager: PackageManager
): LaunchableAppFilter = { app: AppInfo ->
    packageManager.getLaunchIntentForPackage(app.packageName) != null
}.memoize()

typealias IntentAppFilter = AppFilter
@Given
fun IntentAppFilter(
    @Given packageManager: PackageManager,
    @Given intent: Intent
): IntentAppFilter {
    val apps by lazy {
        packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo.packageName }
    }
    return { app -> app.packageName in apps }
}
