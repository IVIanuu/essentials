/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.systemoverlay

import android.content.*
import android.provider.*
import androidx.core.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*

abstract class SystemOverlayPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : SystemOverlayPermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> { Settings.canDrawOverlays(appContext) }

    @Provide fun <P : SystemOverlayPermission> showFindPermissionHint(
      appConfig: AppConfig
    ) = ShowFindPermissionHint<P>(appConfig.sdk >= 30)

    @Provide fun <P : SystemOverlayPermission> intentFactory(
      appConfig: AppConfig
    ) = PermissionIntentFactory<P> {
      Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        "package:${appConfig.packageName}".toUri()
      )
    }
  }
}
