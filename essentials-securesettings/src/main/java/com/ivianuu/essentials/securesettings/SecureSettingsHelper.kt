/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.securesettings

import android.Manifest.permission.WRITE_SECURE_SETTINGS
import android.content.Context
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.injekt.Inject
import com.ivianuu.kommon.core.content.hasPermissions

/**
 * Provides infos about the secure settings access state
 */
@Inject
class SecureSettingsHelper(
    private val context: Context,
    private val shell: Shell
) {
    fun canWriteSecureSettings(): Boolean =
        context.hasPermissions(WRITE_SECURE_SETTINGS)

    suspend fun grantWriteSecureSettings(): Boolean {
        return try {
            shell.run("pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS")
            canWriteSecureSettings()
        } catch (e: Exception) {
            false
        }
    }
}