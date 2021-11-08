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
import androidx.compose.material.Icon
import com.ivianuu.essentials.Res
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide

@Provide object BackActionId : ActionId("back")

@Provide @Res fun backAction() = Action(
  id = BackActionId,
  title = loadResource(R.string.es_action_back),
  permissions = accessibilityActionPermissions,
  icon = singleActionIcon {
    Icon(painterResId = R.drawable.es_ic_action_back)
  }
)

@Provide fun backActionExecutor(
  globalActionExecutor: GlobalActionExecutor
): ActionExecutor<BackActionId> = {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_BACK)
}
