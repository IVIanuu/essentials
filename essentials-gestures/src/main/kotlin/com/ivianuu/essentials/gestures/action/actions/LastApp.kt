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
import com.ivianuu.essentials.accessibility.performGlobalAction
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.delay

//@ActionBinding("last_app")
fun lastAppAction(
    choosePermissions: choosePermissions,
    stringResource: stringResource,
): Action = Action(
    id = "last_app",
    title = stringResource(R.string.es_action_last_app),
    permissions = choosePermissions { listOf(accessibility) },
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_repeat)
)

//@ActionExecutorBinding("last_app")
@GivenFun
suspend fun goToLastApp(performGlobalAction: performGlobalAction) {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    delay(250)
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
