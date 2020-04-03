package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf

@ApplicationScope
@Module
private fun ComponentBuilder.menuAction() {
    action(
        key = "menu",
        title = { getStringResource(R.string.es_action_menu) },
        iconProvider = { SingleActionIconProvider(Icons.Default.MoreVert) },
        permissions = { listOf(actionPermission { root }) },
        executor = { get<RootActionExecutor>(parameters = parametersOf("input keyevent 82")) }
    )
}
