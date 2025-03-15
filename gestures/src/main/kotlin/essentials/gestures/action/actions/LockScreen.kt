/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.gestures.action.*
import injekt.*

@Provide object LockScreenActionId : AccessibilityActionId(
  "lock_screen",
  GLOBAL_ACTION_LOCK_SCREEN
) {
  @Provide val action get() = Action(
    id = LockScreenActionId,
    title = "Lock screen",
    icon = { Icon(Icons.Default.SettingsPower, null) },
    permissions = listOf(ActionAccessibilityPermission::class)
  )
}
