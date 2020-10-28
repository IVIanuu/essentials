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

package com.ivianuu.essentials.permission.installunknownapps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.to

fun InstallUnknownAppsPermission(
    vararg metadata: Permission.Pair<*>
) = Permission(
    Permission.IsUnknownAppsPermission to Unit,
    Permission.Intent to Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES"),
    *metadata
)

val Permission.Companion.IsUnknownAppsPermission by lazy {
    Permission.Key<Unit>("IsUnknownAppsPermission")
}

@PermissionStateProviderBinding
class InstallUnknownAppsPermissionStateProvider(
    private val packageManager: PackageManager,
    private val systemBuildInfo: com.ivianuu.essentials.util.SystemBuildInfo,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsUnknownAppsPermission in permission

    @SuppressLint("NewApi")
    override suspend fun isGranted(permission: Permission): Boolean =
        systemBuildInfo.sdk < 26 || packageManager.canRequestPackageInstalls()
}
