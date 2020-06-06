package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SettingsPower
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@SuppressLint("InlinedApi")
@Module
private fun lockScreenModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             systemBuildInfo: SystemBuildInfo,
             accessibilityExecutorFactory: @Provider (Int) -> AccessibilityActionExecutor,
             rootExecutorFactory: @Provider (String) -> RootActionExecutor ->
        Action(
            key = "lock_screen",
            title = resourceProvider.getString(R.string.es_action_lock_screen),
            iconProvider = SingleActionIconProvider(Icons.Default.SettingsPower),
            permissions = listOf(
                if (systemBuildInfo.sdk >= 28) permissions.accessibility
                else permissions.root
            ),
            executor = if (systemBuildInfo.sdk >= 28) {
                accessibilityExecutorFactory(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
            } else {
                rootExecutorFactory("input keyevent 26")
            }
        ) as @StringKey("lock_screen") Action
    }
}
