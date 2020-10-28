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

package com.ivianuu.essentials.permission.runtime

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionRequestHandlerBinding
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.util.startActivityForResult
import com.ivianuu.injekt.android.ApplicationContext

fun RuntimePermission(
    name: String,
    vararg metadata: Permission.Pair<*>
) = Permission(
    Permission.RuntimePermissionName to name,
    *metadata
)

val Permission.Companion.RuntimePermissionName by lazy {
    Permission.Key<String>("RuntimePermissionName")
}

@PermissionStateProviderBinding
class RuntimePermissionStateProvider(
    private val applicationContext: ApplicationContext,
) : PermissionStateProvider {
    override fun handles(permission: Permission): Boolean =
        Permission.RuntimePermissionName in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        applicationContext.checkSelfPermission(permission[Permission.RuntimePermissionName]) ==
            PackageManager.PERMISSION_GRANTED
}

@PermissionRequestHandlerBinding
class RuntimePermissionRequestHandler(
    private val startActivityForResult: startActivityForResult<String, Boolean>,
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Permission.RuntimePermissionName in permission

    override suspend fun request(permission: Permission) {
        startActivityForResult(
            ActivityResultContracts.RequestPermission(),
            permission[Permission.RuntimePermissionName]
        )
    }
}
