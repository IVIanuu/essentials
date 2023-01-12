/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.packageusagestats

import android.app.AppOpsManager
import android.content.Intent
import android.net.Uri
import android.os.Process
import android.provider.Settings
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide

abstract class PackageUsageStatsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission

@Provide fun <P : PackageUsageStatsPermission> packageUsageStatsShowFindPermissionHint(
) = ShowFindPermissionHint<P>(true)

context(AppOpsManager, BuildInfo)
@Suppress("DEPRECATION")
@Provide
fun <P : PackageUsageStatsPermission> packageUsageStatsPermissionStateProvider(
) = PermissionStateProvider<P> {
  checkOpNoThrow(
    AppOpsManager.OPSTR_GET_USAGE_STATS,
    Process.myUid(),
    packageName
  ) == AppOpsManager.MODE_ALLOWED
}

context(BuildInfo)
@Provide fun <P : PackageUsageStatsPermission> notificationListenerPermissionIntentFactory(
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_USAGE_ACCESS_SETTINGS,
    Uri.parse("package:${packageName}")
  )
}
