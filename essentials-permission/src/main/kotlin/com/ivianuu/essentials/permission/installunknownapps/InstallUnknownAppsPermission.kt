/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
