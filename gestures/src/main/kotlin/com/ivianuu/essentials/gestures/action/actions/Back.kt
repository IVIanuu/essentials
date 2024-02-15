/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.accessibility.AndroidAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object BackActionId : ActionId("back")

@Provide fun backAction(resources: Resources) = Action(
  id = BackActionId,
  title = resources(R.string.es_action_back),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_action_back)
)

@Provide fun backActionExecutor(accessibilityService: AccessibilityService) =
  ActionExecutor<BackActionId> {
    accessibilityService.performGlobalAction(AndroidAccessibilityService.GLOBAL_ACTION_BACK)
  }
