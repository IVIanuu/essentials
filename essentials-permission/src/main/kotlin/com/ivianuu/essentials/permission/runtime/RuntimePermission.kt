/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.runtime

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.injekt.Provide

interface RuntimePermission : Permission {
  val permissionName: String
}

@Provide fun <P : RuntimePermission> runtimePermissionStateProvider(
  context: AppContext
) = PermissionStateProvider<P> { permission ->
  context.checkSelfPermission(permission.permissionName) == PackageManager.PERMISSION_GRANTED
}

@Provide fun <P : RuntimePermission> runtimePermissionRequestHandler(
  context: AppContext,
  navigator: Navigator
) = PermissionRequestHandler<P> { permission ->
  val contract = ActivityResultContracts.RequestPermission()
  navigator.push(contract.createIntent(context, permission.permissionName).toIntentKey())
}
