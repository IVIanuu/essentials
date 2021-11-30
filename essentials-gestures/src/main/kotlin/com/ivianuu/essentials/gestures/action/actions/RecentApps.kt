/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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

@Provide object RecentAppsActionId : ActionId("recent_apps")

@Provide fun recentAppsAction(RP: ResourceProvider) = Action(
  id = RecentAppsActionId,
  title = loadResource(R.string.es_action_recent_apps),
  icon = staticActionIcon(R.drawable.es_ic_action_recent_apps)
)

@Provide fun recentAppsActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<RecentAppsActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
