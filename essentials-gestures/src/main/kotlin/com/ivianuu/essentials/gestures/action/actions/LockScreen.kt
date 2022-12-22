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

@Provide object LockScreenActionId : ActionId("lock_screen")

context(ResourceProvider) @Provide fun lockScreenAction(
  systemBuildInfo: SystemBuildInfo
): Action<LockScreenActionId> = Action(
  id = LockScreenActionId,
  title = loadResource(R.string.es_action_lock_screen),
  icon = staticActionIcon(R.drawable.es_ic_power_settings),
  permissions = listOf(
    if (systemBuildInfo.sdk >= 28) typeKeyOf<ActionAccessibilityPermission>()
    else typeKeyOf<ActionRootPermission>()
  )
)

@SuppressLint("InlinedApi")
@Provide
fun lockScreenActionExecutor(
  actionRootCommandRunner: ActionRootCommandRunner,
  globalActionExecutor: GlobalActionExecutor,
  systemBuildInfo: SystemBuildInfo,
) = ActionExecutor<LockScreenActionId> {
  if (systemBuildInfo.sdk >= 28) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
  } else {
    actionRootCommandRunner("input keyevent 26")
  }
}
