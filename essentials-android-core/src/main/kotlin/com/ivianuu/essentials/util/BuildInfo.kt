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

package com.ivianuu.essentials.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

@Immutable
data class BuildInfo(
    val isDebug: Boolean,
    val packageName: String,
    val versionCode: Int
) {
    companion object {
        @Given(ApplicationScoped::class)
        fun bind(): BuildInfo {
            val appInfo = applicationContext.applicationInfo
            val packageInfo = given<PackageManager>()
                .getPackageInfo(appInfo.packageName, 0)
            return BuildInfo(
                isDebug = appInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE),
                packageName = appInfo.packageName,
                versionCode = packageInfo.versionCode
            )
        }
    }
}
