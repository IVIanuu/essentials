/*
 * Copyright 2021 Manuel Wrage
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
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay

@Provide object LastAppNativeActionId : ActionId("last_app_native")

@Provide fun lastAppNativeAction(RP: ResourceProvider) = Action(
  id = LastAppNativeActionId,
  title = loadResource(R.string.es_action_last_app_native),
  permissions = accessibilityActionPermissions,
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = staticActionIcon(R.drawable.es_ic_repeat)
)

@Provide fun lastAppNativeActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<LastAppNativeActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
  delay(250)
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
