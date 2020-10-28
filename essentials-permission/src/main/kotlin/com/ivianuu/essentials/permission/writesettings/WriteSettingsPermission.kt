/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.permission.writesettings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.to
import com.ivianuu.injekt.android.ApplicationContext

fun WriteSettingsPermission(
    context: Context,
    vararg metadata: Permission.Pair<*>
) = Permission(
    Permission.IsWriteSettingsPermission to Unit,
    Permission.Intent to Intent(
        Settings.ACTION_MANAGE_WRITE_SETTINGS,
        "package:${context.packageName}".toUri()
    ),
    *metadata
)

val Permission.Companion.IsWriteSettingsPermission by lazy {
    Permission.Key<Unit>("IsWriteSettingsPermission")
}

@PermissionStateProviderBinding
class WriteSettingsPermissionStateProvider(
    private val applicationContext: ApplicationContext,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSettingsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        Settings.System.canWrite(applicationContext)
}
