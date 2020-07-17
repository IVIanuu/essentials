package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoAlbum
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.delay
import kotlin.time.milliseconds

@BindAction
@Reader
fun screenshotAction(): Action {
    val systemBuildInfo = given<SystemBuildInfo>()
    return Action(
        key = "screenshot",
        title = Resources.getString(R.string.es_action_screenshot),
        iconProvider = SingleActionIconProvider(Icons.Default.PhotoAlbum),
        permissions = permissions {
            listOf(
                if (systemBuildInfo.sdk >= 28) accessibility
                else root
            )
        },
        executor = (if (systemBuildInfo.sdk >= 28) {
            given<(Int) -> AccessibilityActionExecutor>()(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
        } else {
            given<(String) -> RootActionExecutor>()("input keyevent 26")
        }).let {
            it.beforeAction { delay(500.milliseconds.toLongMilliseconds()) } // todo remove toLongMilliseconds()
        }
    )
}
