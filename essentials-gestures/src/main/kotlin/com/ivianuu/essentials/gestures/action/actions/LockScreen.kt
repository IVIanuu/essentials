/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object LockScreenActionId : ActionId("lock_screen")

@Provide fun lockScreenAction(resources: Resources) = Action(
  id = LockScreenActionId,
  title = resources(R.string.es_action_lock_screen),
  icon = staticActionIcon(R.drawable.es_ic_power_settings),
  permissions = listOf(typeKeyOf<ActionAccessibilityPermission>())
)

@SuppressLint("InlinedApi")
@Provide
fun lockScreenActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<LockScreenActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
}
