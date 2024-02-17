/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesettings

import android.content.*
import android.provider.*
import androidx.core.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*

abstract class WriteSettingsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : WriteSettingsPermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> { Settings.System.canWrite(appContext) }

    @Provide fun <P : WriteSettingsPermission> intentFactory(
      appConfig: AppConfig
    ) = PermissionIntentFactory<P> {
      Intent(
        Settings.ACTION_MANAGE_WRITE_SETTINGS,
        "package:${appConfig.packageName}".toUri()
      )
    }
  }
}
