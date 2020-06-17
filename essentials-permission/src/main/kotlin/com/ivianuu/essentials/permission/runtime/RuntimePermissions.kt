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

package com.ivianuu.essentials.permission.runtime

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.util.StartActivityForResult
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

fun RuntimePermission(
    name: String,
    vararg metadata: KeyWithValue<*>
) = Permission(
    Permission.RuntimePermissionName withValue name,
    *metadata
)

val Permission.Companion.RuntimePermissionName by lazy {
    Permission.Key<String>("RuntimePermissionName")
}

@BindPermissionStateProvider
@Transient
internal class RuntimePermissionStateProvider(
    private val context: @ForApplication Context,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.RuntimePermissionName in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        context.checkSelfPermission(permission[Permission.RuntimePermissionName]) ==
                PackageManager.PERMISSION_GRANTED
}

@BindPermissionRequestHandler
@Transient
internal class RuntimePermissionRequestHandler(
    private val startActivityForResult: StartActivityForResult
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
