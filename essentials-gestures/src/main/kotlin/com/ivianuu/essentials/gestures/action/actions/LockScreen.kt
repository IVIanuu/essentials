package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import com.ivianuu.injekt.parametersOf

@SuppressLint("InlinedApi")
internal val EsLockScreenActionModule = Module {
    bindAction(
        key = "lock_screen",
        title = { getStringResource(R.string.es_action_split_screen) },
        iconProvider = { SingleActionIconProvider(R.drawable.es_ic_power_settings) },
        permissions = {
            listOf(actionPermission {
                if (get<SystemBuildInfo>().sdk >= 28) accessibility
                else root
            })
        },
        executor = {
            if (get<SystemBuildInfo>().sdk >= 28) {
                get<AccessibilityActionExecutor> {
                    parametersOf(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
                }
            } else {
                get<RootActionExecutor> {
                    parametersOf("input keyevent 26")
                }
            }
        }
    )
}
