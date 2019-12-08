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

package com.ivianuu.essentials.permission

import android.content.Context
import android.content.pm.PackageManager
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.allGranted
import com.ivianuu.essentials.ui.common.permissionRequestRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Factory

@Factory
class RuntimePermissionStateProvider(
    private val context: Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        permission.metadata.contains(MetadataKeys.RuntimePermissionName)

    override suspend fun isGranted(permission: Permission): Boolean =
        context.checkSelfPermission(permission.metadata[MetadataKeys.RuntimePermissionName]) ==
                PackageManager.PERMISSION_GRANTED

}

@Factory
class RuntimePermissionRequestHandler(
    @PermissionNavigator private val navigator: Navigator
) : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        permission.metadata.contains(MetadataKeys.RuntimePermissionName)

    override suspend fun request(
        activity: PermissionActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        d { "request $permission" }
        val granted = navigator.push<com.ivianuu.essentials.ui.common.PermissionResult>(
            permissionRequestRoute(
                permissions = setOf(permission.metadata[MetadataKeys.RuntimePermissionName])
            )
        )?.allGranted ?: false

        return PermissionResult(isOk = granted)
    }
}

fun RuntimePermission(
    name: String,
    vararg pairs: Pair<Metadata.Key<*>, Any?>
) = Permission(
    metadata = metadataOf(
        MetadataKeys.RuntimePermissionName to name,
        *pairs
    )
)

val MetadataKeys.RuntimePermissionName by lazy { Metadata.Key<String>("RuntimePermissionName") }