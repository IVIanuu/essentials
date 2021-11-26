/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
