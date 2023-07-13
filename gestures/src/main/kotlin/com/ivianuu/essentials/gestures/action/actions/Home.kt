/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object HomeActionId : ActionId("home")

@Provide fun homeAction(resources: Resources) = Action(
  id = HomeActionId,
  title = resources.resource(R.string.es_action_home),
  permissions = if (needsHomeIntentWorkaround) emptyList()
  else accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_action_home)
)

@Provide fun homeActionExecutor(
  closeSystemDialogs: CloseSystemDialogsUseCase,
  globalActionExecutor: GlobalActionExecutor,
  intentSender: ActionIntentSender
) = ActionExecutor<HomeActionId> {
  if (!needsHomeIntentWorkaround) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_HOME)
  } else {
    closeSystemDialogs()

    intentSender(
      Intent(Intent.ACTION_MAIN).apply {
        addCategory(
          Intent.CATEGORY_HOME
        )
      },
      null
    )
  }
}

private val needsHomeIntentWorkaround = Build.MANUFACTURER == "OnePlus"
