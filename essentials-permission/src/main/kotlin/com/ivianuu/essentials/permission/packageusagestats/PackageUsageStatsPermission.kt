/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.packageusagestats

import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.provider.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

interface PackageUsageStatsPermission : Permission

@Provide fun <P : PackageUsageStatsPermission> packageUsageStatsShowFindPermissionHint(
) = ShowFindPermissionHint<P>(true)

@Suppress("DEPRECATION")
@Provide
fun <P : PackageUsageStatsPermission> packageUsageStatsPermissionStateProvider(
  appOpsManager: @SystemService AppOpsManager,
  buildInfo: BuildInfo,
) = PermissionStateProvider<P> {
  appOpsManager.checkOpNoThrow(
    AppOpsManager.OPSTR_GET_USAGE_STATS,
    Process.myUid(),
    buildInfo.packageName
  ) == AppOpsManager.MODE_ALLOWED
}

@Provide fun <P : PackageUsageStatsPermission> notificationListenerPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_USAGE_ACCESS_SETTINGS,
    Uri.parse("package:${buildInfo.packageName}")
  )
}
