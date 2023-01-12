/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.delay

@Provide object ScreenshotActionId : ActionId("screenshot")

context(ResourceProvider) @Provide fun screenshotAction(systemBuildInfo: SystemBuildInfo) = Action(
  id = ScreenshotActionId,
  title = loadResource(R.string.es_action_screenshot),
  icon = staticActionIcon(R.drawable.es_ic_photo_album),
  permissions = listOf(
    if (systemBuildInfo.sdk >= 28) typeKeyOf<ActionAccessibilityPermission>()
    else typeKeyOf<ActionRootPermission>()
  )
)

context(ActionRootCommandRunner, GlobalActionExecutor)
    @SuppressLint("InlinedApi")
    @Provide
fun screenshotActionExecutor(systemBuildInfo: SystemBuildInfo) =
  ActionExecutor<ScreenshotActionId> {
    delay(500)
    if (systemBuildInfo.sdk >= 28) {
      performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
    } else {
      runActionRootCommand("input keyevent 26")
    }
  }
