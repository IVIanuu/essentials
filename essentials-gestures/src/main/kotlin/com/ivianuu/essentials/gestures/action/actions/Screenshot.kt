package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.given
import kotlinx.coroutines.delay

@GivenAction
fun screenshotAction(): Action {
    val systemBuildInfo = given<SystemBuildInfo>()
    return Action(
        key = "screenshot",
        title = Resources.getString(R.string.es_action_screenshot),
        icon = singleActionIcon(R.drawable.es_ic_photo_album),
        permissions = permissions {
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
}
