/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object BackActionId : ActionId("back")

@Provide fun backAction(resources: Resources) = Action(
  id = BackActionId,
  title = resources(R.string.action_back),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.ic_action_back)
)

@Provide fun backActionExecutor(accessibilityService: AccessibilityService) =
  ActionExecutor<BackActionId> {
    accessibilityService.performGlobalAction(AndroidAccessibilityService.GLOBAL_ACTION_BACK)
  }
