package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.GlobalActions
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.transient

@Module
internal fun <T : Action> bindAccessibilityAction(
    key: String,
    accessibilityAction: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) {
    transient { resourceProvider: ResourceProvider,
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
    bindAction<T>()
}

@Transient
internal class AccessibilityActionExecutor(
    @Assisted private val accessibilityAction: Int,
    private val globalActions: GlobalActions
) : ActionExecutor {
    override suspend fun invoke() {
        globalActions.performAction(accessibilityAction)
    }
}
