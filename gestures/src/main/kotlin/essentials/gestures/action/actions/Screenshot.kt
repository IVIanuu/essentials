/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.accessibility.*
import essentials.gestures.action.*
import injekt.*
import kotlinx.coroutines.*

@Provide object ScreenshotActionId : ActionId("screenshot") {
  @Provide val action
    get() = Action(
      id = ScreenshotActionId,
      title = "Screenshot",
      icon = { Icon(Icons.Default.PhotoAlbum, null) },
      permissions = listOf(ActionAccessibilityPermission::class)
    )

  @Provide fun screenshotActionExecutor(
    accessibilityManager: AccessibilityManager
  ) = ActionExecutor<ScreenshotActionId> {
    delay(500)
    accessibilityManager.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
  }
}
