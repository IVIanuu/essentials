/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*

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
