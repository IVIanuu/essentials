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

abstract class RuntimePermission(
  val permissionName: String,
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : RuntimePermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> { permission ->
      appContext.checkSelfPermission(permission.permissionName) == PackageManager.PERMISSION_GRANTED
    }

    @Provide fun <P : RuntimePermission> requestHandler(
      appContext: AppContext,
      navigator: Navigator
    ) = PermissionRequestHandler<P> { permission ->
      val contract = ActivityResultContracts.RequestPermission()
      val intent = contract.createIntent(appContext, permission.permissionName)
      navigator.push(intent.asScreen())
    }
  }
}
