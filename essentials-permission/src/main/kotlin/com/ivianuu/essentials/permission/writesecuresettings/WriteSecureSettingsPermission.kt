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

package com.ivianuu.essentials.permission.writesecuresettings

import android.Manifest
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

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
