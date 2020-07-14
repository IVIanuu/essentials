package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun MenuModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
executorFactory: (String) -> RootActionExecutor ->
Action(
key = "menu",
title = getString(R.string.es_action_menu),
iconProvider = SingleActionIconProvider(Icons.Default.MoreVert),
            permissions = listOf(permissions.root),
            executor = executorFactory("input keyevent 82")
        ) as @StringKey("menu") Action
    }
}
 */