/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import android.annotation.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import kotlinx.coroutines.*

@Provide object ScreenshotActionId : ActionId("screenshot")

@Provide fun screenshotAction(
  systemBuildInfo: SystemBuildInfo,
  RP: ResourceProvider
): Action<ScreenshotActionId> = Action(
  id = ScreenshotActionId,
  title = loadResource(R.string.es_action_screenshot),
  icon = staticActionIcon(R.drawable.es_ic_photo_album),
  permissions = listOf(
    if (systemBuildInfo.sdk >= 28) typeKeyOf<ActionAccessibilityPermission>()
    else typeKeyOf<ActionRootPermission>()
  )
)

@SuppressLint("InlinedApi")
@Provide
fun screenshotActionExecutor(
  globalActionExecutor: GlobalActionExecutor,
  rootCommandRunner: ActionRootCommandRunner,
  systemBuildInfo: SystemBuildInfo,
) = ActionExecutor<ScreenshotActionId> {
  delay(500)
  if (systemBuildInfo.sdk >= 28) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
  } else {
    rootCommandRunner("input keyevent 26")
  }
}
