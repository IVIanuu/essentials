/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesettings

import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.injekt.Provide

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
