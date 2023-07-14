/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.runtime

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.ui.navigation.DefaultIntentScreen
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

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
      navigator.push(DefaultIntentScreen(intent))
    }
  }
}
