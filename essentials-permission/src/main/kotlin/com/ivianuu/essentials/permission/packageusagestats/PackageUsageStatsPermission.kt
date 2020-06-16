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
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

fun PackageUsageStatsPermission(
    vararg metadata: KeyWithValue<*>
) = Permission(
    Permission.IsPackageUsageStatsPermission withValue Unit,
    Permission.Intent withValue Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
    *metadata
)

val Permission.Companion.IsPackageUsageStatsPermission by lazy {
    Permission.Key<Unit>("IsPackageUsageStatsPermission")
}

@BindPermissionStateProvider
@Transient
internal class PackageUsageStatsPermissionStateProvider(
    private val appOps: AppOpsManager,
    private val context: @ForApplication Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsPackageUsageStatsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean {
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }
}
