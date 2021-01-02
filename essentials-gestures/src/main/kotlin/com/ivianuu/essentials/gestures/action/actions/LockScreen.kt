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
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.GivenFun

//@ActionBinding("lock_screen")
fun lockScreenAction(
    choosePermissions: choosePermissions,
    stringResource: stringResource,
    systemBuildInfo: SystemBuildInfo,
): Action = Action(
    id = "lock_screen",
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
//@ActionExecutorBinding("lock_screen")
@GivenFun
suspend fun doLockScreen(
    performGlobalAction: performGlobalAction,
    runRootCommand: runRootCommand,
    systemBuildInfo: SystemBuildInfo,
) {
    if (systemBuildInfo.sdk >= 28) {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
    } else {
        runRootCommand("input keyevent 26")
    }
}
