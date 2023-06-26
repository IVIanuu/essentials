/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.packageusagestats

import android.app.AppOpsManager
import android.content.Intent
import android.net.Uri
import android.os.Process
import android.provider.Settings
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

abstract class PackageUsageStatsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission {
  companion object {
    @Provide fun <P : PackageUsageStatsPermission> showFindPermissionHint() =
      ShowFindPermissionHint<P>(true)

    @Suppress("DEPRECATION")
    @Provide
    fun <P : PackageUsageStatsPermission> stateProvider(
      appConfig: AppConfig,
      appOpsManager: @SystemService AppOpsManager,
    ) = PermissionStateProvider<P> {
      appOpsManager.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        appConfig.packageName
      ) == AppOpsManager.MODE_ALLOWED
    }

    @Provide fun <P : PackageUsageStatsPermission> intentFactory(appConfig: AppConfig) =
      PermissionIntentFactory<P> {
        Intent(
          Settings.ACTION_USAGE_ACCESS_SETTINGS,
          Uri.parse("package:${appConfig.packageName}")
        )
      }
  }
}
