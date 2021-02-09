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

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import com.ivianuu.essentials.accessibility.performGlobalAction
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun

object LockScreenActionId : ActionId("lock_screen")

@ActionBinding<LockScreenActionId>
@Given
fun lockScreenAction(
    @Given choosePermissions: choosePermissions,
    @Given stringResource: stringResource,
    @Given systemBuildInfo: SystemBuildInfo,
): Action = Action(
    id = LockScreenActionId,
    title = stringResource(R.string.es_action_lock_screen),
    icon = singleActionIcon(R.drawable.es_ic_power_settings),
    permissions = choosePermissions {
        listOf(
            if (systemBuildInfo.sdk >= 28) accessibility
            else root
        )
    }
)

@SuppressLint("InlinedApi")
@ActionExecutorBinding<LockScreenActionId>
@GivenFun
suspend fun doLockScreen(
    @Given performGlobalAction: performGlobalAction,
    @Given runRootCommand: runRootCommand,
    @Given systemBuildInfo: SystemBuildInfo,
) {
    if (systemBuildInfo.sdk >= 28) {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
    } else {
        runRootCommand("input keyevent 26")
    }
}
