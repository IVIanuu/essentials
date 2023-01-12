/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.ignorebatteryoptimizations

import android.annotation.SuppressLint
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

abstract class IgnoreBatteryOptimizationsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission

context(BuildInfo)
    @Provide fun <P : IgnoreBatteryOptimizationsPermission> ignoreBatteryOptimizationsPermissionStateProvider(
  powerManager: @SystemService PowerManager
) = PermissionStateProvider<P> {
  powerManager.isIgnoringBatteryOptimizations(packageName)
}

context(BuildInfo)
@SuppressLint("BatteryLife")
@Provide
fun <P : IgnoreBatteryOptimizationsPermission> ignoreBatteryOptimizationsPermissionIntentFactory(
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
    "package:${packageName}".toUri()
  )
}
