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

abstract class WriteSettingsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : WriteSettingsPermission> state(
      appContext: AppContext
    ): PermissionState<P> = Settings.System.canWrite(appContext)

    @Provide fun <P : WriteSettingsPermission> requestParams(
      appConfig: AppConfig
    ) = IntentPermissionRequestParams<P>(
      Intent(
        Settings.ACTION_MANAGE_WRITE_SETTINGS,
        "package:${appConfig.packageName}".toUri()
      )
    )
  }
}
