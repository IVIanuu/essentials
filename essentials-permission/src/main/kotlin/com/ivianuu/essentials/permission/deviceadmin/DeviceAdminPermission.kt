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
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionStateProviderIntoSet
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import kotlin.reflect.KClass

fun DeviceAdminPermission(
    context: Context,
    deviceAdminClass: KClass<*>,
    explanation: String,
    vararg metadata: MetaDataKeyWithValue<*>
): Permission {
    val component = ComponentName(context, deviceAdminClass.java)
    return Permission(
        metadata = metadataOf(
            Metadata.DeviceAdminComponent withValue component,
            Metadata.Intent withValue Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, explanation)
            },
            *metadata
        )
    )
}

val Metadata.Companion.DeviceAdminComponent by lazy {
    Metadata.Key<ComponentName>("DeviceAdminComponent")
}

@ApplicationScope
@Module
private fun ComponentBuilder.deviceAdminPermission() {
    bindPermissionStateProviderIntoSet<DeviceAdminPermissionStateProvider>()
}

@Factory
private class DeviceAdminPermissionStateProvider(
    private val devicePolicyManager: DevicePolicyManager
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.DeviceAdminComponent in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        devicePolicyManager.isAdminActive(permission.metadata[Metadata.DeviceAdminComponent])
}
