/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import android.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

abstract class WriteSecureSettingsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission {
  @Provide companion object {
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
      navigator.push(WriteSecureSettingsScreen(permissionKey))
    }
  }
}
