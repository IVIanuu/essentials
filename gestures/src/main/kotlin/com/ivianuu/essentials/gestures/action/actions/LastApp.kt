/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
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
      icon = staticActionIcon(R.drawable.ic_repeat)
    )

  @Provide fun executor(
    accessibilityService: AccessibilityService
  ) = ActionExecutor<LastAppActionId> {
    accessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS)
    delay(100)
    accessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS)
  }
}
