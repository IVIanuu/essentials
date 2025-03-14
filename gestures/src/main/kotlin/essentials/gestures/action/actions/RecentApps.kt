/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material3.*
import androidx.compose.ui.res.*
import essentials.Scope
import essentials.accessibility.*
import essentials.gestures.R
import essentials.gestures.action.*
import injekt.*

@Provide object RecentAppsActionId : ActionId("recent_apps") {
  @Provide val action
    get() = Action(
      id = RecentAppsActionId,
      title = "Recent apps",
      icon = { Icon(painterResource(R.drawable.ic_action_recent_apps), null) }
    )

  @Provide suspend fun execute(scope: Scope<*> = inject): ActionExecutorResult<RecentAppsActionId> {
    performGlobalAccessibilityAction(GLOBAL_ACTION_RECENTS)
  }
}
