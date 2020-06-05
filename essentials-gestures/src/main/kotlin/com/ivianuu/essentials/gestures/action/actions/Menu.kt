package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun MenuModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             executorFactory: @Provider (String) -> RootActionExecutor ->
        Action(
            key = "menu",
            title = resourceProvider.getString(R.string.es_action_menu),
            iconProvider = SingleActionIconProvider(Icons.Default.MoreVert),
            permissions = listOf(permissions.root),
            executor = executorFactory("input keyevent 82")
        ) as @StringKey("menu") Action
    }
}
