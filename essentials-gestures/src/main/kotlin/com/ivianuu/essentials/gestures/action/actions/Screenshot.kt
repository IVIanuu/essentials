/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  icon = singleActionIcon(R.drawable.es_ic_photo_album),
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
): ActionExecutor<ScreenshotActionId> = {
  delay(500)
  if (systemBuildInfo.sdk >= 28) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
  } else {
    rootCommandRunner("input keyevent 26")
  }
}
