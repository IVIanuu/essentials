package com.ivianuu.essentials.gestures.action.actions

// todo

/**
import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.bindAction
import kotlinx.coroutines.delay

val ScreenshotActionModule = Module {
    bindAction(
        key = "screenshot",
        title = { stringResource(R.string.action_split_screen) },
        iconProvider = { SingleActionIconProvider(R.drawable.es_ic_power_settings) },
        executor = {
            val executor = if (get<SystemBuildInfo>().sdk >= 24) {
                get<AccessibilityActionExecutor> {
                    parametersOf(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
                }
            } else {
                get<RootActionExecutor> {
                    parametersOf("input keyevent 26")
                }
            }

            return@bindAction executor.beforeAction { delay(500) }
        }
    )
}
*/