package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Repeat
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import kotlinx.coroutines.delay

@GivenAction
fun lastAppAction() = Action(
    key = "last_app",
    title = Resources.getString(R.string.es_action_last_app),
    permissions = permissions { listOf(accessibility) },
    unlockScreen = true,
    icon = singleActionIcon(Icons.Default.Repeat),
    execute = {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
        delay(250)
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }
)
