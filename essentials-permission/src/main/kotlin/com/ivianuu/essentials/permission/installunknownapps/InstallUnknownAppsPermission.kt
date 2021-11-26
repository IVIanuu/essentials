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

package com.ivianuu.essentials.permission.installunknownapps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.injekt.Provide

interface InstallUnknownAppsPermission : Permission

@SuppressLint("NewApi")
@Provide
fun <P : InstallUnknownAppsPermission> installUnknownAppsPermissionStateProvider(
  packageManager: PackageManager,
  systemBuildInfo: SystemBuildInfo
) = PermissionStateProvider<P> {
  systemBuildInfo.sdk < 26 || packageManager.canRequestPackageInstalls()
}

@Provide fun <P : InstallUnknownAppsPermission> installUnknownAppsPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> {
  Intent(
    "android.settings.MANAGE_UNKNOWN_APP_SOURCES",
    Uri.parse("package:${buildInfo.packageName}")
  )
}
