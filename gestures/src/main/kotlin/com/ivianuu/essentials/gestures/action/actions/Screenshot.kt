/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*

@Provide object ScreenshotActionId : ActionId("screenshot") {
  @Provide val action
    get() = Action(
      id = ScreenshotActionId,
      title = "Screenshot",
      icon = { Icon(Icons.Default.PhotoAlbum, null) },
      permissions = listOf(typeKeyOf<ActionAccessibilityPermission>())
    )

  @Provide fun screenshotActionExecutor(
    accessibilityService: AccessibilityService
  ) = ActionExecutor<ScreenshotActionId> {
    delay(500)
    accessibilityService.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
  }
}
