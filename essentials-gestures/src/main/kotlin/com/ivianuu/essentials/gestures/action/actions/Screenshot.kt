package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn

@SuppressLint("InlinedApi")
@Module
private fun ScreenshotModule() {
    installIn<ApplicationComponent>()
    /*action<@ActionQualifier("screenshot") Action>(
        key = "screenshot",
        title = { getStringResource(R.string.es_action_screenshot) },
        iconProvider = { SingleActionIconProvider(Icons.Default.PhotoAlbum) },
        permissions = {
            listOf(actionPermission {
                if (get<SystemBuildInfo>().sdk >= 28) accessibility
                else root
            })
        },
        executor = {
            val executor = if (get<SystemBuildInfo>().sdk >= 28) {
                get<@Provider (Int) -> AccessibilityActionExecutor>()(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
            } else {
                get<@Provider (String) -> RootActionExecutor>()("input keyevent 26")
            }

            return@action executor.beforeAction { delay(500) }
        }
    )*/
}
