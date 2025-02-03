/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object SplitScreenActionId : ActionId("split_screen") {
  @Provide val action
    get() = Action(
      id = SplitScreenActionId,
      title = "Split screen",
      permissions = accessibilityActionPermissions,
      icon = { Icon(Icons.Default.ViewAgenda, null) }
    )

  @Provide fun executor(
    accessibilityManager: AccessibilityManager
  ) = ActionExecutor<SplitScreenActionId> {
    accessibilityManager.performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
  }
}
