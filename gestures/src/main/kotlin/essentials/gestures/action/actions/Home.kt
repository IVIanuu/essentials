/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import android.content.*
import android.os.*
import essentials.accessibility.*
import essentials.gestures.R
import essentials.gestures.action.*
import injekt.*

@Provide object HomeActionId : ActionId("home") {
  @Provide val action
    get() = Action(
      id = HomeActionId,
      title = "Home",
      permissions = if (needsHomeIntentWorkaround) emptyList()
      else accessibilityActionPermissions,
      icon = staticActionIcon(R.drawable.ic_action_home)
    )

  @Provide fun homeActionExecutor(
    accessibilityManager: AccessibilityManager,
    intentSender: ActionIntentSender,
    systemDialogController: SystemDialogController,
  ) = ActionExecutor<HomeActionId> {
    if (!needsHomeIntentWorkaround) {
      accessibilityManager.performGlobalAction(GLOBAL_ACTION_HOME)
    } else {
      systemDialogController.closeSystemDialogs()

      intentSender.sendIntent(
        Intent(Intent.ACTION_MAIN).apply {
          addCategory(
            Intent.CATEGORY_HOME
          )
        },
        null
      )
    }
  }

  private val needsHomeIntentWorkaround get() = Build.MANUFACTURER == "OnePlus"
}
