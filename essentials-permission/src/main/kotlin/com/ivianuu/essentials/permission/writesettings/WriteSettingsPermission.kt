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

interface WriteSettingsPermission : Permission

@Provide fun <P : WriteSettingsPermission> writeSettingsPermissionStateProvider(
  context: AppContext
) = PermissionStateProvider<P> { Settings.System.canWrite(context) }

@Provide fun <P : WriteSettingsPermission> writeSettingsPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_MANAGE_WRITE_SETTINGS,
    "package:${buildInfo.packageName}".toUri()
  )
}
