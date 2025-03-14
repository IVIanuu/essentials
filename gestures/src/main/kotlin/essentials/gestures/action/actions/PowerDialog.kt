/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.*
import essentials.accessibility.*
import essentials.gestures.action.*
import injekt.*

@Provide object PowerDialogActionId : ActionId("power_dialog") {
  @Provide val action get() = Action(
    id = PowerDialogActionId,
    title = "Power dialog",
    permissions = accessibilityActionPermissions,
    icon = { Icon(Icons.Default.PowerSettingsNew, null) }
  )

  @Provide suspend fun execute(scope: Scope<*> = inject): ActionExecutorResult<PowerDialogActionId> {
    performGlobalAccessibilityAction(GLOBAL_ACTION_POWER_DIALOG)
  }
}
