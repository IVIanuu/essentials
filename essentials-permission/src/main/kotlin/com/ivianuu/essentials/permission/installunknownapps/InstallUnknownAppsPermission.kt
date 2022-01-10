/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
