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

package com.ivianuu.essentials.processrestart

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.ApplicationContext

@GivenFun
suspend fun restartProcess(
    applicationContext: ApplicationContext,
    buildInfo: BuildInfo,
    logger: Logger,
    packageManager: PackageManager,
) {
    val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
        .addFlags(FLAG_ACTIVITY_NEW_TASK)
    logger.d { "restart process $intent" }
    ProcessRestartActivity.launch(applicationContext, intent)
    Runtime.getRuntime().exit(0)
}
