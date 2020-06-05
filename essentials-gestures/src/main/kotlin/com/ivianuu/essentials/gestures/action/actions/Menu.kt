package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn

@Module
private fun MenuModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("menu") Action>(
        key = "menu",
        title = { getStringResource(R.string.es_action_menu) },
        iconProvider = { SingleActionIconProvider(Icons.Default.MoreVert) },
        permissions = { listOf(actionPermission { root }) },
        executor = { get<@Provider (String) -> RootActionExecutor>()("input keyevent 82") }
    )*/
}
