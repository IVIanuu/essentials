/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object RecentAppsActionId : ActionId("recent_apps")

@Provide fun recentAppsAction(resources: Resources) = Action(
  id = RecentAppsActionId,
  title = resources(R.string.action_recent_apps),
  icon = staticActionIcon(R.drawable.ic_action_recent_apps)
)

@Provide fun recentAppsActionExecutor(
  accessibilityService: AccessibilityService
) = ActionExecutor<RecentAppsActionId> {
  accessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS)
}
