/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.systemoverlay

import android.content.*
import android.provider.*
import androidx.core.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*

interface SystemOverlayPermission : Permission

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
