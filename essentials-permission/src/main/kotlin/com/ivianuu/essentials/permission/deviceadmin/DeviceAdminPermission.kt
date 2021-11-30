/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
