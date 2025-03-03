/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import android.annotation.*
import android.content.*
import android.os.*
import android.provider.*
import androidx.compose.runtime.*
import androidx.core.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import injekt.*

abstract class IgnoreBatteryOptimizationsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : IgnoreBatteryOptimizationsPermission> stateProvider(
      appConfig: AppConfig,
      powerManager: @SystemService PowerManager
    ) = PermissionStateProvider<P> {
      powerManager.isIgnoringBatteryOptimizations(appConfig.packageName)
    }

    @SuppressLint("BatteryLife")
    @Provide
    fun <P : IgnoreBatteryOptimizationsPermission> intentFactory(
      appConfig: AppConfig
    ) = PermissionIntentFactory<P> {
      Intent(
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        "package:${appConfig.packageName}".toUri()
      )
    }
  }
}
