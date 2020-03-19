package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Repeat
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.delay

internal fun ComponentBuilder.lastAppAction() {
    action(
        key = "last_app",
        title = { getStringResource(R.string.es_action_last_app) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Repeat) },
        permissions = { listOf(actionPermission { accessibility }) },
        unlockScreen = { true },
        executor = { get<LastAppActionExecutor>() }
    )
}

@Factory
private class LastAppActionExecutor(
    private val context: Context,
    private val lazyRecentAppsExecutor: Lazy<AccessibilityActionExecutor>
) : ActionExecutor {
    override suspend fun invoke() {
        val executor =
            lazyRecentAppsExecutor(parameters = parametersOf(AccessibilityService.GLOBAL_ACTION_RECENTS))
        executor()
        delay(250)
        executor()
    }
}
