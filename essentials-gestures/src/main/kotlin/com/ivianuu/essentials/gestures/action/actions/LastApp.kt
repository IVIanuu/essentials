package com.ivianuu.essentials.gestures.action.actions

// todo

/**
import android.accessibilityservice.AccessibilityService
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.gestures.data.Flag
import kotlinx.coroutines.delay

val LastAppActionModule = Module {
    bindAction(
        key = "last_app",
        title = { stringResource(R.string.action_last_app) },
        iconProvider = {},
        executor = { get }
    )
}

@Factory
class LastAppActionExecutor(
    accessibilityActionExecutorProvider: Provider<AccessibilityActionExecutor>
) : ActionExecutor {
    private val recentAppsActionExecutor = accessibilityActionExecutorProvider {
        parametersOf(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }

    override suspend fun invoke() {
        recentAppsActionExecutor()
        delay(250)
        recentAppsActionExecutor()
    }
}

private fun createLastAppAction() = Action(
    key = KEY_LAST_APP,
    title = string(R.string.action_last_app),
    states = stateless(R.drawable.es_ic_repeat),
    flags = setOf(
        Flag.RequiresAccessibilityPermission,
        Flag.RequiresSdk24,
        Flag.UnlockScreen
    )
)*/