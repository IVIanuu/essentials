/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import android.content.*
import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object HomeActionId : ActionId("home")

@Provide fun homeAction(RP: ResourceProvider) = Action(
  id = HomeActionId,
  title = loadResource(R.string.es_action_home),
  permissions = if (needsHomeIntentWorkaround) emptyList()
  else accessibilityActionPermissions,
  icon = staticActionIcon(R.drawable.es_ic_action_home)
)

@Provide fun homeActionExecutor(
  actionIntentSender: ActionIntentSender,
  closeSystemDialogs: CloseSystemDialogsUseCase,
  globalActionExecutor: GlobalActionExecutor,
) = ActionExecutor<HomeActionId> {
  if (!needsHomeIntentWorkaround) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_HOME)
  } else {
    closeSystemDialogs()

    actionIntentSender(
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
