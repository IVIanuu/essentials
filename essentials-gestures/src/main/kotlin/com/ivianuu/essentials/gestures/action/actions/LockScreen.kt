package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.stringResource

@ActionBinding
fun lockScreenAction(
    choosePermissions: choosePermissions,
    performGlobalAction: performGlobalAction,
    runRootCommand: runRootCommand,
    stringResource: stringResource,
    systemBuildInfo: SystemBuildInfo,
): Action = Action(
    key = "lock_screen",
    title = stringResource(R.string.es_action_lock_screen),
    icon = singleActionIcon(R.drawable.es_ic_power_settings),
    permissions = choosePermissions {
        listOf(
            if (systemBuildInfo.sdk >= 28) accessibility
            else root
        )
    },
    execute = {
        if (systemBuildInfo.sdk >= 28) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
        } else {
            runRootCommand("input keyevent 26")
        }
    }
)
