package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun LastAppModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             executor: LastAppActionExecutor ->
        Action(
key = "last_app",
title = getString(R.string.es_action_last_app),
permissions = listOf(permissions.accessibility),
            unlockScreen = true,
            iconProvider = SingleActionIconProvider(Icons.Default.Repeat),
            executor = executor
        ) as @StringKey("last_app") Action
    }
}

@Unscoped
internal class LastAppActionExecutor(
    private val lazyRecentAppsExecutor: @Provider (Int) -> AccessibilityActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        val executor =
            lazyRecentAppsExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
        executor()
        delay(250)
        executor()
    }
}
 */