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

@Provide object LockScreenActionId : ActionId("lock_screen") {
  @Provide val lockScreenAction get() = Action(
      id = LockScreenActionId,
      title = "Lock screen",
      icon = { Icon(Icons.Default.SettingsPower, null) },
      permissions = listOf(ActionAccessibilityPermission::class)
    )

  @Provide fun executor(
    accessibilityManager: AccessibilityManager
  ) = ActionExecutor<LockScreenActionId> {
    accessibilityManager.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
  }
}

