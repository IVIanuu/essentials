package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn

@SuppressLint("InlinedApi")
@Module
private fun lockScreenModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("lock_screen") Action>(
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
                get<@Provider (Int) -> AccessibilityActionExecutor>()(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
            } else {
                get<@Provider (String) -> RootActionExecutor>()("input keyevent 26")
            }
        }
    )*/
}
