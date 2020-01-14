package com.ivianuu.essentials.gestures.action.actions

// todo

/**
import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.gestures.data.Flag
import kotlin.collections.setOf

val LockScreenActionModule = Module {
    bindAction(
        key = "lock_screen",
        title = { stringResource(R.string.action_split_screen) },
        iconProvider = { SingleActionIconProvider(R.drawable.es_ic_power_settings) },
        flags = {
            setOf(
                if (get<SystemBuildInfo>().sdk >= 28) {
                    Flag.RequiresAccessibilityPermission
                } else {
                    Flag.RequiresRoot
                }
            )
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
*/