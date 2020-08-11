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
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.permission.GivenPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.withValue

fun SystemOverlayPermission(
    context: Context,
    vararg metadata: KeyWithValue<*>
) = Permission(
    Permission.IsSystemOverlayPermission withValue Unit,
    Permission.Intent withValue Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        "package:${context.packageName}".toUri()
    ),
    *metadata
)

val Permission.Companion.IsSystemOverlayPermission by lazy {
    Permission.Key<Unit>("IsSystemOverlayPermission")
}

@GivenPermissionStateProvider
internal class SystemOverlayPermissionStateProvider : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsSystemOverlayPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        Settings.canDrawOverlays(applicationContext)

}
