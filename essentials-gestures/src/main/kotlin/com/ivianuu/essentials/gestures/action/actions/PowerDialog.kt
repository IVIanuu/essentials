/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object PowerDialogActionId : ActionId("power_dialog")

context(ResourceProvider) @Provide fun powerDialogAction() = Action(
  id = PowerDialogActionId,
  title = loadResource(R.string.es_action_power_dialog),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_power_settings_new)
)

context(GlobalActionExecutor)
    @Provide fun powerDialogActionExecutor() = ActionExecutor<PowerDialogActionId> {
  performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
}
