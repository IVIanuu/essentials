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
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.delay

object LastAppActionId : ActionId("last_app")

@ActionBinding<LastAppActionId>
@Given
fun lastAppAction(
    @Given choosePermissions: choosePermissions,
    @Given stringResource: stringResource,
): Action = Action(
    id = "last_app",
    title = stringResource(R.string.es_action_last_app),
    permissions = choosePermissions { listOf(accessibility) },
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_repeat)
)

@ActionExecutorBinding<LastAppActionId>
@GivenFun
suspend fun goToLastApp(@Given performGlobalAction: performGlobalAction) {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    delay(250)
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
