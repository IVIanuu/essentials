/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import android.annotation.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide object LockScreenActionId : ActionId("lock_screen") {
  @Provide val lockScreenAction get() = Action(
      id = LockScreenActionId,
      title = "Lock screen",
      icon = staticActionIcon(R.drawable.ic_power_settings),
      permissions = listOf(typeKeyOf<ActionAccessibilityPermission>())
    )

  @Provide fun executor(
    accessibilityService: AccessibilityService
  ) = ActionExecutor<LockScreenActionId> {
    accessibilityService.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
  }
}

