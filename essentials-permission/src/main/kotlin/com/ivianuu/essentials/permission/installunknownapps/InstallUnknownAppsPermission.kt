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

package com.ivianuu.essentials.permission.installunknownapps

import android.content.Context
import android.content.Intent
import android.os.Build
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

fun InstallUnknownAppsPermission(
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.IsUnknownAppsPermission withValue Unit,
        Metadata.Intent withValue Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES"), // todo Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
        *metadata
    )
)

val Metadata.Companion.IsUnknownAppsPermission by lazy {
    Metadata.Key<Unit>("IsUnknownAppsPermission")
}

@ApplicationScope
@Module
private fun ComponentBuilder.installUnknownAppsPermission() {
    bindPermissionStateProviderIntoSet<InstallUnknownAppsPermissionStateProvider>()
}

@Factory
private class InstallUnknownAppsPermissionStateProvider(
    private val context: Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsUnknownAppsPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        Build.VERSION.SDK_INT < 26 || context.packageManager.canRequestPackageInstalls()
}
