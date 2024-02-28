/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
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

  @Provide fun executor(
    accessibilityManager: AccessibilityManager
  ) = ActionExecutor<LastAppActionId> {
    accessibilityManager.performGlobalAction(GLOBAL_ACTION_RECENTS)
    delay(100)
    accessibilityManager.performGlobalAction(GLOBAL_ACTION_RECENTS)
  }
}
