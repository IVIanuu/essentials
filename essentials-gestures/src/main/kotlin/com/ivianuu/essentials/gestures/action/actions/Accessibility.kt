package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.gestures.GlobalActions
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.ui.painter.VectorRenderable
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.get
import com.ivianuu.injekt.parametersOf

fun ModuleBuilder.bindAccessibilityAction(
    key: String,
    accessibilityAction: Int,
    titleRes: Int,
    icon: VectorAsset
) {
    bindAction(
        key = key,
        title = { getStringResource(titleRes) },
        iconProvider = { SingleActionIconProvider(icon) },
        permissions = { listOf(actionPermission { accessibility }) },
        executor = {
            get<AccessibilityActionExecutor> { parametersOf(accessibilityAction) }
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
