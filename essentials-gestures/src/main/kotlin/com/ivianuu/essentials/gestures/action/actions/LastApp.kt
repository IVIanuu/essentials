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
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import kotlinx.coroutines.delay

@Given
object LastAppActionId : ActionId("last_app")

@ActionBinding<LastAppActionId>
@Given
fun lastAppAction(
    @Given resourceProvider: ResourceProvider,
) = Action(
    id = "last_app",
    title = resourceProvider.string(R.string.es_action_last_app),
    permissions = accessibilityActionPermissions,
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_repeat)
)

@ActionExecutorBinding<LastAppActionId>
@Given
fun lastAppActionExecutor(@Given globalActionExecutor: GlobalActionExecutor): ActionExecutor = {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
    delay(250)
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
