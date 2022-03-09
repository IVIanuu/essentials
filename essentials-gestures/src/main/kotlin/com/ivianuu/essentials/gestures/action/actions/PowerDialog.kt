/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*

@Provide object PowerDialogActionId : ActionId("power_dialog")

@Provide fun powerDialogAction(RP: ResourceProvider) = Action(
  id = PowerDialogActionId,
  title = loadResource(R.string.es_action_power_dialog),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_power_settings_new)
)

@Provide fun powerDialogActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<PowerDialogActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
}
