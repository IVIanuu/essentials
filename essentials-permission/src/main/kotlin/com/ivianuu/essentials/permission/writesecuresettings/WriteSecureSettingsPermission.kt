/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

abstract class WriteSecureSettingsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: @Composable () -> Unit = { Permission.NullIcon }
) : Permission {
  companion object {
    @Provide fun <P : WriteSecureSettingsPermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> {
      appContext
        .checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    @Provide fun <P : WriteSecureSettingsPermission> requestHandler(
      navigator: Navigator,
      permissionKey: TypeKey<P>
    ) = PermissionRequestHandler<P> {
      navigator.push(WriteSecureSettingsKey(permissionKey))
    }
  }
}

