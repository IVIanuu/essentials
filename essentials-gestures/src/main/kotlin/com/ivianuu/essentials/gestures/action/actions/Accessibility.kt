package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.GlobalActions
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun ComponentBuilder.bindAccessibilityAction(
    key: String,
    accessibilityAction: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) {
    action(
        key = key,
        title = { getStringResource(titleRes) },
        iconProvider = { SingleActionIconProvider(icon) },
        permissions = { listOf(actionPermission { accessibility }) },
        executor = {
            get<AccessibilityActionExecutor>(parameters = parametersOf(accessibilityAction))
        }
    )
}

@Factory
class AccessibilityActionExecutor(
    @Param private val accessibilityAction: Int,
    private val globalActions: GlobalActions
) : ActionExecutor {
    override suspend fun invoke() {
        globalActions.performAction(accessibilityAction)
    }
}
