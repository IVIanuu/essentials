/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.accessibility.AndroidAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object SplitScreenActionId : ActionId("split_screen")

@Provide fun splitScreenAction(resources: Resources) = Action(
  id = SplitScreenActionId,
  title = resources(R.string.es_action_split_screen),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_view_agenda)
)

@Provide fun splitScreenActionExecutor(
  accessibilityService: AccessibilityService
) = ActionExecutor<SplitScreenActionId> {
  accessibilityService.performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
}
