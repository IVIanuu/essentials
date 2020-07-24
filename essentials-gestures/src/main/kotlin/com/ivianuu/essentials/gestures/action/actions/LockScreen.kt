package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SettingsPower
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@BindAction
@Reader
fun lockScreenAction(): Action {
    val systemBuildInfo = given<SystemBuildInfo>()
    return Action(
        key = "lock_screen",
        title = Resources.getString(R.string.es_action_lock_screen),
        icon = singleActionIcon(Icons.Default.SettingsPower),
        permissions = permissions {
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
}
