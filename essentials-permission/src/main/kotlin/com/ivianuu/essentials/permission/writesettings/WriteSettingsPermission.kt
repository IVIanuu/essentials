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

package com.ivianuu.essentials.permission.writesettings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader

fun WriteSettingsPermission(
    context: Context,
    vararg metadata: KeyWithValue<*>
) = Permission(
    Permission.IsWriteSettingsPermission withValue Unit,
    Permission.Intent withValue Intent(
        Settings.ACTION_MANAGE_WRITE_SETTINGS,
        "package:${context.packageName}".toUri()
    ),
    *metadata
)

val Permission.Companion.IsWriteSettingsPermission by lazy {
    Permission.Key<Unit>("IsWriteSettingsPermission")
}

@Given
internal class WriteSettingsPermissionStateProvider : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.IsWriteSettingsPermission in permission

    override suspend fun isGranted(permission: Permission): Boolean =
        Settings.System.canWrite(applicationContext)

}
