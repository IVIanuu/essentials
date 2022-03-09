/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import android.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*

interface WriteSecureSettingsPermission : Permission

@Provide fun <P : WriteSecureSettingsPermission> writeSecureSettingsPermissionStateProvider(
  context: AppContext
) = PermissionStateProvider<P> {
  context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
      PackageManager.PERMISSION_GRANTED
}

@Provide fun <P : WriteSecureSettingsPermission> writeSecureSettingsPermissionsRequestHandler(
  navigator: Navigator,
  permissionKey: TypeKey<P>
) = PermissionRequestHandler<P> {
  navigator.push(WriteSecureSettingsKey(permissionKey))
}
