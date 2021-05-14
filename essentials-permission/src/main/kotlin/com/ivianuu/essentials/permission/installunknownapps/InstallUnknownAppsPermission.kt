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

package com.ivianuu.essentials.permission.installunknownapps

import android.annotation.*
import android.content.*
import android.content.pm.*
import android.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*

interface InstallUnknownAppsPermission : Permission

@SuppressLint("NewApi")
@Given
fun <P : InstallUnknownAppsPermission> installUnknownAppsPermissionStateProvider(
  @Given packageManager: PackageManager,
  @Given systemBuildInfo: SystemBuildInfo
): PermissionStateProvider<P> = {
  systemBuildInfo.sdk < 26 || packageManager.canRequestPackageInstalls()
}

@Given fun <P : InstallUnknownAppsPermission> installUnknownAppsPermissionIntentFactory(
  @Given buildInfo: BuildInfo
): PermissionIntentFactory<P> = {
  Intent(
    "android.settings.MANAGE_UNKNOWN_APP_SOURCES",
    Uri.parse("package:${buildInfo.packageName}")
  )
}
