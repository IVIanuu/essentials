/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object BackActionId : ActionId("back")

context(ResourceProvider) @Provide fun backAction() = Action(
  id = BackActionId,
  title = loadResource(R.string.es_action_back),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_action_back)
)

context(GlobalActionExecutor) @Provide fun backActionExecutor() = ActionExecutor<BackActionId> {
  performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}
