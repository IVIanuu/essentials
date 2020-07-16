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

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

object EsAndroidUtilModule {

    @Given(ApplicationComponent::class)
    @Reader
    fun buildInfo(): BuildInfo {
        val appInfo = given<Application>().applicationInfo
        val packageInfo = given<PackageManager>()
            .getPackageInfo(appInfo.packageName, 0)
        return BuildInfo(
            isDebug = appInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE),
            packageName = appInfo.packageName,
            versionCode = packageInfo.versionCode
        )
    }

    @Given
    @Reader
    fun deviceInfo() = DeviceInfo(model = Build.MODEL, manufacturer = Build.MANUFACTURER)

    @Given
    @Reader
    fun logger() = if (given<BuildInfo>().isDebug) {
        given<AndroidLogger>()
    } else {
        given<NoopLogger>()
    }

    @Given
    @Reader
    fun systemBuildInfo() = SystemBuildInfo(Build.VERSION.SDK_INT)

}
