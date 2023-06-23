/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay

@Provide object LastAppActionId : ActionId("last_app")

@Provide fun lastAppAction(resources: Resources) = Action(
  id = LastAppActionId,
  title = resources(R.string.es_action_last_app),
  permissions = accessibilityActionPermissions,
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = staticActionIcon(R.drawable.es_ic_repeat)
)

@Provide fun lastAppActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<LastAppActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
  delay(100)
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
