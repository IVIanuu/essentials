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
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionStateProviderIntoSet
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory

fun PackageUsageStatsPermission(
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.IsPackageUsageStatsPermission withValue Unit,
        Metadata.Intent withValue Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
        *metadata
    )
)

val Metadata.Companion.IsPackageUsageStatsPermission by lazy {
    Metadata.Key<Unit>("IsPackageUsageStatsPermission")
}

internal fun ComponentBuilder.packageUsageStatsPermission() {
    bindPermissionStateProviderIntoSet<PackageUsageStatsPermissionStateProvider>()
}

@Factory
private class PackageUsageStatsPermissionStateProvider(
    private val appOps: AppOpsManager,
    private val context: Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsPackageUsageStatsPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean {
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }
}
