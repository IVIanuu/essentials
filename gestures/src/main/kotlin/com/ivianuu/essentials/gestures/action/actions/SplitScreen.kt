/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object SplitScreenActionId : ActionId("split_screen") {
  @Provide val action
    get() = Action(
      id = SplitScreenActionId,
      title = "Split screen",
      permissions = accessibilityActionPermissions,
      icon = staticActionIcon(R.drawable.ic_view_agenda)
    )

  @Provide fun executor(
    accessibilityService: AccessibilityService
  ) = ActionExecutor<SplitScreenActionId> {
    accessibilityService.performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
  }
}
