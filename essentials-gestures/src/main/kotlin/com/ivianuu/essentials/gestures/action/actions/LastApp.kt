package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Repeat
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.delay

@BindAction
@Reader
fun lastAppAction() = Action(
    key = "last_app",
    title = Resources.getString(R.string.es_action_last_app),
    permissions = permissions { listOf(accessibility) },
    unlockScreen = true,
    iconProvider = SingleActionIconProvider(Icons.Default.Repeat),
    executor = given<LastAppActionExecutor>()
)

@Given
internal class LastAppActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        val executor =
            given<AccessibilityActionExecutor>(AccessibilityService.GLOBAL_ACTION_RECENTS)
        executor()
        delay(250)
        executor()
    }
}
