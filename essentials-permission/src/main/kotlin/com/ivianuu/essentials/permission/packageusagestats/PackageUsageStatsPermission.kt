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

package com.ivianuu.essentials.permission.packageusagestats

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.MetadataKeys
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.util.Clock
import com.ivianuu.injekt.Factory

fun PackageUsageStatsPermission(
    vararg pairs: Pair<Metadata.Key<*>, Any?>
) = Permission(
    metadata = metadataOf(
        MetadataKeys.IsPackageUsageStatsPermission to Unit,
        MetadataKeys.Intent to Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
        *pairs
    )
)

val MetadataKeys.IsPackageUsageStatsPermission by lazy {
    Metadata.Key<Unit>("IsPackageUsageStatsPermission")
}

@Factory
class PackageUsageStatsPermissionStateProvider(
    private val appOps: AppOpsManager,
    private val context: Context,
    private val clock: Clock,
    private val usageStatsManager: UsageStatsManager
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        permission.metadata.contains(MetadataKeys.IsPackageUsageStatsPermission)

    override suspend fun isGranted(permission: Permission): Boolean {
        val mode = appOps.checkOpNoThrow(
            "android:get_usage_stats",
            android.os.Process.myUid(), context.packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            return false
        }

        // Verify that access is possible. Some devices "lie" and return MODE_ALLOWED even when it's not.
        val now = clock.currentTimeMillis
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - 1000 * 10,
            now
        )
        return stats != null && stats.isNotEmpty()
    }
}