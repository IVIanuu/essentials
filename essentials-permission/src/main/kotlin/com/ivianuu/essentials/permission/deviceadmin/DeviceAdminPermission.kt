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

package com.ivianuu.essentials.permission.deviceadmin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given
import kotlin.reflect.KClass

fun DeviceAdminPermission(
    context: Context,
    deviceAdminClass: KClass<*>,
    explanation: String,
    vararg metadata: KeyWithValue<*>
): Permission {
    val component = ComponentName(context, deviceAdminClass.java)
    return Permission(
        Permission.DeviceAdminComponent withValue component,
        Permission.Intent withValue Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, explanation)
        },
        *metadata
    )
}

val Permission.Companion.DeviceAdminComponent by lazy {
    Permission.Key<ComponentName>("DeviceAdminComponent")
}

@BindPermissionStateProvider
@Given
internal class DeviceAdminPermissionStateProvider : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.DeviceAdminComponent in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        given<DevicePolicyManager>().isAdminActive(permission[Permission.DeviceAdminComponent])
}
