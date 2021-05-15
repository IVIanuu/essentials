/*
 * Copyright 2020 Manuel Wrage
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

import android.*
import android.content.pm.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.common.*

interface WriteSecureSettingsPermission : Permission

@Given fun <P : WriteSecureSettingsPermission> writeSecureSettingsPermissionStateProvider(
  @Given appContext: AppContext
): PermissionStateProvider<P> = {
  appContext.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
      PackageManager.PERMISSION_GRANTED
}

@Given fun <P : WriteSecureSettingsPermission> writeSecureSettingsPermissionsRequestHandler(
  @Given navigator: Navigator,
  @Given permissionKey: TypeKey<P>
): PermissionRequestHandler<P> = {
  navigator.push(WriteSecureSettingsKey(permissionKey))
}
