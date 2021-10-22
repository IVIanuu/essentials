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
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide

@Provide object HomeActionId : ActionId("home")

@Provide fun homeAction(rp: ResourceProvider): Action<HomeActionId> = Action(
  id = HomeActionId,
  title = loadResource(R.string.es_action_home),
  permissions = if (needsHomeIntentWorkaround) emptyList()
  else accessibilityActionPermissions,
  icon = singleActionIcon(R.drawable.es_ic_action_home)
)

@Provide fun homeActionExecutor(
  actionIntentSender: ActionIntentSender,
  closeSystemDialogs: CloseSystemDialogsUseCase,
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor,
): ActionExecutor<HomeActionId> = {
  if (!needsHomeIntentWorkaround) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_HOME)
  } else {
    closeSystemDialogs()

    actionIntentSender(
      Intent(Intent.ACTION_MAIN).apply {
        addCategory(
          Intent.CATEGORY_HOME
        )
      },
      false,
      null
    )
  }
}

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"
