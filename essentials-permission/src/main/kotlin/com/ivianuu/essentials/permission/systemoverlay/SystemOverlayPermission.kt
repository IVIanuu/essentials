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

package com.ivianuu.essentials.permission.systemoverlay

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module

fun SystemOverlayPermission(
    context: Context,
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.IsSystemOverlayPermission withValue Unit,
        Metadata.Intent withValue Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri()
        ),
        *metadata
    )
)

val Metadata.Companion.IsSystemOverlayPermission by lazy {
    Metadata.Key<Unit>("IsSystemOverlayPermission")
}

internal val EsSystemOverlayPermissionModule = Module {
    bindPermissionStateProvider<SystemOverlayPermissionStateProvider>()
}

@Factory
internal class SystemOverlayPermissionStateProvider(
    private val context: Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.IsSystemOverlayPermission in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        Settings.canDrawOverlays(context)
}
