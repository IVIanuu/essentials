/*
 * Copyright 2021 Manuel Wrage
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

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlin.reflect.KClass

interface DeviceAdminPermission : Permission {
  val deviceAdminClass: KClass<out DeviceAdminReceiver>
  val explanation: String
}

@Provide fun <P : DeviceAdminPermission> deviceAdminPermissionStateProvider(
  context: AppContext,
  devicePolicyManager: @SystemService DevicePolicyManager
) = PermissionStateProvider<P> { permission ->
  devicePolicyManager.isAdminActive(ComponentName(context, permission.deviceAdminClass.java))
}

@Provide fun <P : DeviceAdminPermission> deviceAdminPermissionIntentFactory(
  context: AppContext
) = PermissionIntentFactory<P> { permission ->
  Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
    putExtra(
      DevicePolicyManager.EXTRA_DEVICE_ADMIN,
      ComponentName(context, permission.deviceAdminClass.java)
    )
    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, permission.explanation)
  }
}
