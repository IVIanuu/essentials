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

package com.ivianuu.essentials.permission.root

import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionRequestHandlerBinding
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.shell.isShellAvailable
import com.ivianuu.essentials.util.showToastRes

fun RootPermission(vararg metadata: Permission.Pair<*>) = Permission(
    Permission.IsRootPermission to Unit,
    *metadata
)

val Permission.Companion.IsRootPermission by lazy {
    Permission.Key<Unit>("IsRootPermission")
}

@PermissionStateProviderBinding
class RootPermissionStateProvider(
    private val isShellAvailable: isShellAvailable,
) : PermissionStateProvider {
    override fun handles(permission: Permission): Boolean =
        Permission.IsRootPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean = isShellAvailable()
}

@PermissionRequestHandlerBinding
class RootPermissionRequestHandler(
    private val isShellAvailable: isShellAvailable,
    private val showToastRes: showToastRes,
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Permission.IsRootPermission in permission

    override suspend fun request(permission: Permission) {
        val isOk = isShellAvailable()
        if (!isOk) showToastRes(R.string.es_no_root)
    }
}
