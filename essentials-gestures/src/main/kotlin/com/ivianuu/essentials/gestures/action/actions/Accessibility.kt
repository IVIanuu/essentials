package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.GlobalActions
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient

@Module
internal fun <T : Action> bindAccessibilityAction(
    key: String,
    accessibilityAction: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) {
    action { resourceProvider: ResourceProvider,
                actionPermissions: ActionPermissions,
                accessibilityActionExecutorProvider: @Provider (Int) -> AccessibilityActionExecutor ->
        Action(
            key = key,
            title = resourceProvider.getString(titleRes),
            iconProvider = SingleActionIconProvider(icon),
            permissions = listOf(actionPermissions.accessibility),
            executor = accessibilityActionExecutorProvider(accessibilityAction)
        ) as T
    }
}

@Transient
internal class AccessibilityActionExecutor(
    private val accessibilityAction: @Assisted Int,
    private val globalActions: GlobalActions
) : ActionExecutor {
    override suspend fun invoke() {
        globalActions.performAction(accessibilityAction)
    }
}
