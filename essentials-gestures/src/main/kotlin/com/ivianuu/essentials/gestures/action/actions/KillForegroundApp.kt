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

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.gestures.action.plus
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.first

@ActionBinding
fun killCurrentAction(
    choosePermissions: choosePermissions,
    killCurrentApp: killCurrentApp,
    stringResource: stringResource,
): Action = Action(
    key = "kill_current_app_action",
    title = stringResource(R.string.es_action_kill_current_app),
    icon = singleActionIcon(Icons.Default.Clear),
    permissions = choosePermissions { accessibility + root },
    execute = { killCurrentApp() }
)

@FunBinding
suspend fun killCurrentApp(
    buildInfo: BuildInfo,
    currentAppFlow: CurrentApp,
    getHomePackage: getHomePackage,
    runRootCommand: runRootCommand,
) {
    val currentApp = currentAppFlow.first()
    if (currentApp != "android" &&
        currentApp != "com.android.systemui" &&
        currentApp != buildInfo.packageName && // we have no suicidal intentions :D
        currentApp != getHomePackage()
    ) {
        runRootCommand("am force-stop $currentApp")
    }
}

@FunBinding
fun getHomePackage(
    packageManager: PackageManager,
): String {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }
    return packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName ?: ""
}
