/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.ignorebatteryoptimizations

import android.annotation.*
import android.content.*
import android.os.*
import android.provider.*
import androidx.core.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

interface IgnoreBatteryOptimizationsPermission : Permission

@Provide
fun <P : IgnoreBatteryOptimizationsPermission> ignoreBatteryOptimizationsPermissionStateProvider(
  buildInfo: BuildInfo,
  powerManager: @SystemService PowerManager
) = PermissionStateProvider<P> {
  powerManager.isIgnoringBatteryOptimizations(buildInfo.packageName)
}

@SuppressLint("BatteryLife")
@Provide
fun <P : IgnoreBatteryOptimizationsPermission> ignoreBatteryOptimizationsPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
    "package:${buildInfo.packageName}".toUri()
  )
}
