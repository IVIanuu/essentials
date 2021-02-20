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

package com.ivianuu.essentials.securesettings

import android.Manifest.permission.WRITE_SECURE_SETTINGS
import android.content.pm.PackageManager
import com.ivianuu.essentials.result.getOrElse
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext

interface SecureSettingsPermission {

    suspend fun isGranted(): Boolean

    suspend fun grantViaRoot(): Boolean

}

@Given
class SecureSettingsPermissionImpl(
    @Given private val appContext: AppContext,
    @Given private val buildInfo: BuildInfo,
    @Given private val shell: Shell
) : @Given SecureSettingsPermission {

    override suspend fun isGranted(): Boolean =
        appContext.checkSelfPermission(WRITE_SECURE_SETTINGS) ==
                PackageManager.PERMISSION_GRANTED

    override suspend fun grantViaRoot(): Boolean = runKatching {
        shell.run("pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS")
        isGranted()
    }.getOrElse { false }

}
