package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SettingsPower
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.parametersOf

@SuppressLint("InlinedApi")
internal fun ComponentBuilder.lockScreenAction() {
    action(
        key = "lock_screen",
        title = { getStringResource(R.string.es_action_lock_screen) },
        iconProvider = { SingleActionIconProvider(Icons.Default.SettingsPower) },
        permissions = {
            listOf(actionPermission {
                if (get<SystemBuildInfo>().sdk >= 28) accessibility
                else root
            })
        },
        executor = {
            if (get<SystemBuildInfo>().sdk >= 28) {
                get<AccessibilityActionExecutor>(parameters = parametersOf(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN))
            } else {
                get<RootActionExecutor>(parameters = parametersOf("input keyevent 26"))
            }
        }
    )
}
