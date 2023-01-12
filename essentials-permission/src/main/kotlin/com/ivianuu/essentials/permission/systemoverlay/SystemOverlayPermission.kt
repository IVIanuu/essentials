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
import com.ivianuu.injekt.inject

abstract class SystemOverlayPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission

context(AppContext)
    @Provide fun <P : SystemOverlayPermission> systemOverlayPermissionStateProvider() =
  PermissionStateProvider<P> { Settings.canDrawOverlays(inject()) }

context(SystemBuildInfo)
    @Provide fun <P : SystemOverlayPermission> systemOverlayShowFindPermissionHint() =
  ShowFindPermissionHint<P>(systemSdk >= 30)

context(BuildInfo)
@Provide fun <P : SystemOverlayPermission> systemOverlayPermissionIntentFactory(
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    "package:${packageName}".toUri()
  )
}
