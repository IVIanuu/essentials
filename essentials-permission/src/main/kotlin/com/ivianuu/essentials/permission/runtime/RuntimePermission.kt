/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.runtime

import android.content.pm.*
import androidx.activity.result.contract.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

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
