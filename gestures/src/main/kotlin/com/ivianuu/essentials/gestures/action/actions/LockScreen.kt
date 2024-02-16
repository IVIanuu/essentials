/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN
import android.annotation.SuppressLint
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object LockScreenActionId : ActionId("lock_screen")

@Provide fun lockScreenAction(resources: Resources) = Action(
  id = LockScreenActionId,
  title = resources(R.string.action_lock_screen),
  icon = staticActionIcon(R.drawable.ic_power_settings),
  permissions = listOf(typeKeyOf<ActionAccessibilityPermission>())
)

@SuppressLint("InlinedApi")
@Provide
fun lockScreenActionExecutor(
  accessibilityService: AccessibilityService
) = ActionExecutor<LockScreenActionId> {
  accessibilityService.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
}
