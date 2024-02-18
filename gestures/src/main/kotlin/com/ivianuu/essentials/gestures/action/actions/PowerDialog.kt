/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object PowerDialogActionId : ActionId("power_dialog") {
  @Provide val action get() = Action(
    id = PowerDialogActionId,
    title = "Power dialog",
    permissions = accessibilityActionPermissions,
    icon = { Icon(Icons.Default.PowerSettingsNew, null) }
  )

  @Provide fun executor(
    accessibilityService: AccessibilityService
  ) = ActionExecutor<PowerDialogActionId> {
    accessibilityService.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
  }
}
