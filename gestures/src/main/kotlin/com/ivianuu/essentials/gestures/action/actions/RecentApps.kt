/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object RecentAppsActionId : ActionId("recent_apps") {
  @Provide val action
    get() = Action(
      id = RecentAppsActionId,
      title = "Recent apps",
      icon = staticActionIcon(R.drawable.ic_action_recent_apps)
    )

  @Provide fun executor(
    accessibilityManager: AccessibilityManager
  ) = ActionExecutor<RecentAppsActionId> {
    accessibilityManager.performGlobalAction(GLOBAL_ACTION_RECENTS)
  }
}
