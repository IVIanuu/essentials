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
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide object LockScreenActionId : ActionId("lock_screen")

@Provide fun lockScreenAction(
  systemBuildInfo: SystemBuildInfo,
  RP: ResourceProvider
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
