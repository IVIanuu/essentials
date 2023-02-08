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
import kotlinx.coroutines.delay

@Provide object LastAppNativeActionId : ActionId("last_app_native")

@Provide fun lastAppNativeAction(resourceProvider: ResourceProvider) = Action(
  id = LastAppNativeActionId,
  title = resourceProvider(R.string.es_action_last_app_native),
  permissions = accessibilityActionPermissions,
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = staticActionIcon(R.drawable.es_ic_repeat)
)

@Provide fun lastAppNativeActionExecutor(
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<LastAppNativeActionId> {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
  delay(250)
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
}
