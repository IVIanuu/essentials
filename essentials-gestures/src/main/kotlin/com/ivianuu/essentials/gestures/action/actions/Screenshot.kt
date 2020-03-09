package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.gestures.action.bindAction
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoAlbum
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.delay

@SuppressLint("InlinedApi")
internal fun ComponentBuilder.esScreenshotActionBindings() {
    bindAction(
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
                get<AccessibilityActionExecutor>(parameters = parametersOf(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT))
            } else {
                get<RootActionExecutor>(parameters = parametersOf("input keyevent 26"))
            }

            return@bindAction executor.beforeAction { delay(500) }
        }
    )
}
