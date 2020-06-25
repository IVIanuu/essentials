package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoAlbum
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
import kotlinx.coroutines.delay
import kotlin.time.milliseconds

@SuppressLint("InlinedApi")
@Module
private fun ScreenshotModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             accessibilityExecutorFactory: @Provider (Int) -> AccessibilityActionExecutor,
             rootExecutorFactory: @Provider (String) -> RootActionExecutor,
             systemBuildInfo: SystemBuildInfo ->
        Action(
            key = "screenshot",
            title = resourceProvider.getString(R.string.es_action_screenshot),
            iconProvider = SingleActionIconProvider(Icons.Default.PhotoAlbum),
            permissions = listOf(
                if (systemBuildInfo.sdk >= 28) permissions.accessibility
                else permissions.root
            ),
            executor = (if (systemBuildInfo.sdk >= 28) {
                accessibilityExecutorFactory(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
            } else {
                rootExecutorFactory("input keyevent 26")
            }).let {
                it.beforeAction { delay(500.milliseconds.toLongMilliseconds()) } // todo remove toLongMilliseconds()
            }
        ) as @StringKey("screenshot") Action
    }
}
