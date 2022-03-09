/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*

@Provide object BackActionId : ActionId("back")

@Provide fun backAction(RP: ResourceProvider) = Action(
  id = BackActionId,
  title = loadResource(R.string.es_action_back),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_action_back)
)

@Provide fun backActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<BackActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_BACK)
}
