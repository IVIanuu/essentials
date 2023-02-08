/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.systemoverlay

import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide

abstract class SystemOverlayPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission

@Provide fun <P : SystemOverlayPermission> systemOverlayPermissionStateProvider(
  context: AppContext
) = PermissionStateProvider<P> { Settings.canDrawOverlays(context) }

@Provide fun <P : SystemOverlayPermission> systemOverlayShowFindPermissionHint(
  systemBuildInfo: SystemBuildInfo
) = ShowFindPermissionHint<P>(systemBuildInfo.sdk >= 30)

@Provide fun <P : SystemOverlayPermission> systemOverlayPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    "package:${buildInfo.packageName}".toUri()
  )
}
