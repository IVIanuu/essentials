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

package com.ivianuu.essentials.securesettings

import android.Manifest.permission.WRITE_SECURE_SETTINGS
import android.content.Context
import android.content.pm.PackageManager
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

/**
 * Provides infos about the secure settings access state
 */
@Transient
class SecureSettingsHelper(
    private val buildInfo: BuildInfo,
    private val context: @ForApplication Context,
    private val shell: Shell
) {

    fun canWriteSecureSettings(): Boolean =
        context.checkSelfPermission(WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

    suspend fun grantWriteSecureSettingsViaRoot(): Boolean {
        return try {
            shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
            canWriteSecureSettings()
        } catch (e: Exception) {
            false
        }
    }

}
