/*
 * Copyright 2019 Manuel Wrage
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
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionStateProviderIntoSet
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module

@SuppressLint("BatteryLife")
fun IgnoreBatteryOptimizationsPermission(
    context: Context,
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.IgnoreBatteryOptimizationsPermission withValue Unit,
        Metadata.Intent withValue Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            "package:${context.packageName}".toUri()
        ),
        *metadata
    )
)

val Metadata.Companion.IgnoreBatteryOptimizationsPermission by lazy {
    Metadata.Key<Unit>("IgnoreBatteryOptimizationsPermission")
}

@ApplicationScope
@Module
private fun ComponentBuilder.ignoreBatteryOptimizationsPermission() {
    bindPermissionStateProviderIntoSet<IgnoreBatteryOptimizationsPermissionStateProvider>()
}

@Factory
private class IgnoreBatteryOptimizationsPermissionStateProvider(
    private val context: Context,
    private val powerManager: PowerManager
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IgnoreBatteryOptimizationsPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
}
