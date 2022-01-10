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
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide

@Provide object SplitScreenActionId : ActionId("split_screen")

@Provide fun splitScreenAction(RP: ResourceProvider) = Action(
  id = SplitScreenActionId,
  title = loadResource(R.string.es_action_split_screen),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_view_agenda)
)

@Provide fun splitScreenActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<SplitScreenActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
}
