/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object HomeActionId : ActionId("home")

context(ResourceProvider) @Provide fun homeAction() = Action(
  id = HomeActionId,
  title = loadResource(R.string.es_action_home),
  permissions = if (needsHomeIntentWorkaround) emptyList()
  else accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_action_home)
)

context(ActionIntentSender, CloseSystemDialogsUseCase, GlobalActionExecutor)
    @Provide fun homeActionExecutor() = ActionExecutor<HomeActionId> {
  if (!needsHomeIntentWorkaround) {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
  } else {
    closeSystemDialogs()

    sendIntent(
      Intent(Intent.ACTION_MAIN).apply {
        addCategory(
          Intent.CATEGORY_HOME
        )
      },
      false,
      null
    )
  }
}

private val needsHomeIntentWorkaround = Build.MANUFACTURER == "OnePlus"
