/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.accessibility.*
import essentials.gestures.action.*
import injekt.*

@Provide object SplitScreenActionId : ActionId("split_screen") {
  @Provide val action
    get() = Action(
      id = SplitScreenActionId,
      title = "Split screen",
      permissions = accessibilityActionPermissions,
      icon = { Icon(Icons.Default.ViewAgenda, null) }
    )

  @Provide suspend fun execute(
    performAction: performGlobalAccessibilityAction
  ): ActionExecutorResult<SplitScreenActionId> {
    performAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
  }
}
