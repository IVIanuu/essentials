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
import android.content.pm.PackageManager
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

object SecureSettings {

    @Reader
    fun canWrite() = androidApplicationContext.checkSelfPermission(WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    @Reader
    suspend fun grantPermissionViaRoot(): Boolean {
        return try {
            Shell.run("pm grant ${given<BuildInfo>().packageName} android.permission.WRITE_SECURE_SETTINGS")
            canWrite()
        } catch (t: Throwable) {
            false
        }
    }

}
