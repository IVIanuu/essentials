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
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

object KillCurrentAppActionId : ActionId("kill_current_app")

@ActionBinding<KillCurrentAppActionId>
@Given
fun killCurrentAction(
    @Given choosePermissions: choosePermissions,
    @Given stringResource: stringResource,
): Action = Action(
    id = KillCurrentAppActionId,
    title = stringResource(R.string.es_action_kill_current_app),
    icon = singleActionIcon(Icons.Default.Clear),
    permissions = choosePermissions { accessibility + root }
)

@ActionExecutorBinding<KillCurrentAppActionId>
@GivenFun
suspend fun killCurrentApp(
    @Given buildInfo: BuildInfo,
    @Given currentAppFlow: Flow<CurrentApp>,
    @Given getHomePackage: getHomePackage,
    @Given runRootCommand: runRootCommand,
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

@GivenFun
fun getHomePackage(@Given packageManager: PackageManager): String {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }
    return packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName ?: ""
}
