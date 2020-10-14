package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.stringResource
import kotlinx.coroutines.delay

@ActionBinding
fun screenshotAction(
    choosePermissions: choosePermissions,
    performGlobalAction: performGlobalAction,
    runRootCommand: runRootCommand,
    systemBuildInfo: com.ivianuu.essentials.util.SystemBuildInfo,
    stringResource: stringResource,
): Action = Action(
    key = "screenshot",
    title = stringResource(R.string.es_action_screenshot),
    icon = singleActionIcon(R.drawable.es_ic_photo_album),
    permissions = choosePermissions {
        listOf(
            if (systemBuildInfo.sdk >= 28) accessibility
            else root
        )
    },
    execute = {
        delay(500)
        if (systemBuildInfo.sdk >= 28) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
        } else {
            runRootCommand("input keyevent 26")
        }
    }
)
