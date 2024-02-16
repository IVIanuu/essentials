/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT
import android.annotation.SuppressLint
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.delay

@Provide object ScreenshotActionId : ActionId("screenshot")

@Provide fun screenshotAction(resources: Resources) = Action(
  id = ScreenshotActionId,
  title = resources(R.string.action_screenshot),
  icon = staticActionIcon(R.drawable.ic_photo_album),
  permissions = listOf(typeKeyOf<ActionAccessibilityPermission>())
)

@SuppressLint("InlinedApi")
@Provide
fun screenshotActionExecutor(
  accessibilityService: AccessibilityService
) = ActionExecutor<ScreenshotActionId> {
  delay(500)
  accessibilityService.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
}
