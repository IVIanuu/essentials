package com.ivianuu.essentials.gestures.action.actions

/**
@SuppressLint("InlinedApi")
@Module
fun ScreenshotModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             accessibilityExecutorFactory: @Provider (Int) -> AccessibilityActionExecutor,
             rootExecutorFactory: @Provider (String) -> RootActionExecutor,
             systemBuildInfo: SystemBuildInfo ->
        Action(
key = "screenshot",
title = getString(R.string.es_action_screenshot),
iconProvider = SingleActionIconProvider(Icons.Default.PhotoAlbum),
            permissions = listOf(
                if (systemBuildInfo.sdk >= 28) permissions.accessibility
                else permissions.root
            ),
            executor = (if (systemBuildInfo.sdk >= 28) {
                accessibilityExecutorFactory(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
            } else {
                rootExecutorFactory("input keyevent 26")
            }).let {
                it.beforeAction { delay(500.milliseconds.toLongMilliseconds()) } // todo remove toLongMilliseconds()
            }
        ) as @StringKey("screenshot") Action
    }
}
 */