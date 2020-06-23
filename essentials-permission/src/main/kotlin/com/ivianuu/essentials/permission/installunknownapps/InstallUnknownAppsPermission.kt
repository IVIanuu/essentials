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

package com.ivianuu.essentials.permission.installunknownapps

import android.content.Context
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

fun InstallUnknownAppsPermission(
    vararg metadata: KeyWithValue<*>
) = Permission(
    Permission.IsUnknownAppsPermission withValue Unit,
    Permission.Intent withValue Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES"),
    *metadata
)

val Permission.Companion.IsUnknownAppsPermission by lazy {
    Permission.Key<Unit>("IsUnknownAppsPermission")
}

@BindPermissionStateProvider
@Transient
internal class InstallUnknownAppsPermissionStateProvider(
    private val context: @ForApplication Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsUnknownAppsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        Build.VERSION.SDK_INT < 26 || context.packageManager.canRequestPackageInstalls()
}
