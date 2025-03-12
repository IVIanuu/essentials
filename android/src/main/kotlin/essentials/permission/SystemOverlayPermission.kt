/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.content.*
import android.provider.*
import androidx.compose.runtime.*
import androidx.core.net.*
import essentials.*
import injekt.*

abstract class SystemOverlayPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : SystemOverlayPermission> state(
      appContext: AppContext
    ): PermissionState<P> = Settings.canDrawOverlays(appContext)

    @Provide fun <P : SystemOverlayPermission> requestParams(
      appConfig: AppConfig
    ) = IntentPermissionRequestParams<P>(
      intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        "package:${appConfig.packageName}".toUri()
      ),
      showFindHint = appConfig.sdk >= 30
    )
  }
}
