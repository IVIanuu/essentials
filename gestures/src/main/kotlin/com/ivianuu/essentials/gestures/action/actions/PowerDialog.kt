/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_POWER_DIALOG
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object PowerDialogActionId : ActionId("power_dialog")

@Provide fun powerDialogAction(resources: Resources) = Action(
  id = PowerDialogActionId,
  title = resources(R.string.action_power_dialog),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.ic_power_settings_new)
)

@Provide fun powerDialogActionExecutor(
  accessibilityService: AccessibilityService
) = ActionExecutor<PowerDialogActionId> {
  accessibilityService.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
}
