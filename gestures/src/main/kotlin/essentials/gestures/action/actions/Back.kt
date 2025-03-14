/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material3.*
import androidx.compose.ui.res.*
import essentials.*
import essentials.accessibility.*
import essentials.gestures.R
import essentials.gestures.action.*
import injekt.*

@Provide object BackActionId : ActionId("back") {
  @Provide val action get() = Action(
    id = BackActionId,
    title = "Back",
    permissions = accessibilityActionPermissions,
    icon = { Icon(painterResource(R.drawable.ic_action_back), null) }
  )

  @Provide suspend fun execute(scope: Scope<*> = inject):
      ActionExecutorResult<BackActionId> {
    performGlobalAccessibilityAction(GLOBAL_ACTION_BACK)
  }
}
