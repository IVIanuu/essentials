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

package com.ivianuu.essentials.permission.packageusagestats

import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

interface PackageUsageStatsPermission : Permission

@Suppress("DEPRECATION")
@Given
fun <P : PackageUsageStatsPermission> packageUsageStatsPermissionStateProvider(
    @Given appOpsManager: AppOpsManager,
    @Given buildInfo: BuildInfo,
): PermissionStateProvider<P> = {
    appOpsManager.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        buildInfo.packageName
    ) == AppOpsManager.MODE_ALLOWED
}

@Given
fun <P : PackageUsageStatsPermission> notificationListenerPermissionIntentFactory():
        PermissionIntentFactory<P> = { Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS) }
