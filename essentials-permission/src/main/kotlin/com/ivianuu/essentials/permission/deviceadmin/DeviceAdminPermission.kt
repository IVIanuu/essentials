/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.deviceadmin

import android.app.admin.*
import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlin.reflect.*

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
