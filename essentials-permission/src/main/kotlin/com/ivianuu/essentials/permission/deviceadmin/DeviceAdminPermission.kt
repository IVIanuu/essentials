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

package com.ivianuu.essentials.permission.deviceadmin

import android.app.admin.*
import android.content.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlin.reflect.*

interface DeviceAdminPermission : Permission {
    val deviceAdminClass: KClass<out DeviceAdminReceiver>
    val explanation: String
}

@Given
fun <P : DeviceAdminPermission> deviceAdminPermissionStateProvider(
    @Given context: AppContext,
    @Given devicePolicyManager: @SystemService DevicePolicyManager
): PermissionStateProvider<P> = { permission ->
    devicePolicyManager.isAdminActive(ComponentName(context, permission.deviceAdminClass.java))
}

@Given
fun <P : DeviceAdminPermission> deviceAdminPermissionIntentFactory(
    @Given context: AppContext
): PermissionIntentFactory<P> = { permission ->
    Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(context, permission.deviceAdminClass.java))
        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, permission.explanation)
    }
}
