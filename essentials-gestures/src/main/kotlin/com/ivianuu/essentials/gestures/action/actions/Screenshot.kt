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
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.delay

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
