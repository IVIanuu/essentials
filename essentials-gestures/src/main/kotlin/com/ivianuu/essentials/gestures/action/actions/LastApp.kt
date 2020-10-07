package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.Resources
import kotlinx.coroutines.delay

@ActionBinding
fun lastAppAction(
    choosePermissions: choosePermissions,
    performGlobalAction: performGlobalAction,
    resources: Resources,
): Action = Action(
    key = "last_app",
    title = resources.getString(R.string.es_action_last_app),
    permissions = choosePermissions { listOf(accessibility) },
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_repeat),
    execute = {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
        delay(250)
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }
)
