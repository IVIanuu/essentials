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
import kotlinx.coroutines.*

@Provide object LastAppActionId : ActionId("last_app") {
  @Provide val action
    get() = Action(
      id = LastAppActionId,
      title = "Last app",
      permissions = accessibilityActionPermissions,
      unlockScreen = true,
      closeSystemDialogs = true,
      icon = { Icon(Icons.Default.Repeat, null) }
    )

  @Provide suspend fun execute(
    performAction: performGlobalAccessibilityAction
  ): ActionExecutorResult<LastAppActionId> {
    performAction(GLOBAL_ACTION_RECENTS)
    delay(100)
    performAction(GLOBAL_ACTION_RECENTS)
  }
}
