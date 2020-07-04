package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.gestures.action.getString
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.get
import com.ivianuu.injekt.unscoped

@Module
internal fun <T : Action> bindAccessibilityAction(
    key: String,
    accessibilityAction: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) {
    unscoped {
        Action(
            key = key,
            title = getString(titleRes),
            iconProvider = SingleActionIconProvider(icon),
            permissions = permissions { listOf(accessibility) },
            executor = get<@Provider (Int) -> AccessibilityActionExecutor>()(accessibilityAction)
        ) as T
    }
    bindAction<T>()
}

@Unscoped
internal class AccessibilityActionExecutor(
    @Assisted private val accessibilityAction: Int,
    private val accessibilityServices: AccessibilityServices
) : ActionExecutor {
    override suspend fun invoke() {
        accessibilityServices.performGlobalAction(accessibilityAction)
    }
}
