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

@Provide object RecentAppsActionId : ActionId("recent_apps")

@Provide fun recentAppsAction(resources: Resources) = Action(
  id = RecentAppsActionId,
  title = resources(R.string.es_action_recent_apps),
  icon = staticActionIcon(R.drawable.es_ic_action_recent_apps)
)

@Provide fun recentAppsActionExecutor(globalActionExecutor: GlobalActionExecutor) =
  ActionExecutor<RecentAppsActionId> {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
  }