package com.ivianuu.essentials.gestures.action.actions

/**
@SuppressLint("InlinedApi")
@Module
fun lockScreenModule() {
installIn<ApplicationComponent>()
action { resourceProvider: ResourceProvider,
permissions: ActionPermissions,
systemBuildInfo: SystemBuildInfo,
accessibilityExecutorFactory: (Int) -> AccessibilityActionExecutor,
rootExecutorFactory: (String) -> RootActionExecutor ->
Action(
key = "lock_screen",
title = getString(R.string.es_action_lock_screen),
iconProvider = SingleActionIconProvider(Icons.Default.SettingsPower),
permissions = listOf(
if (systemBuildInfo.sdk >= 28) permissions.accessibility
else permissions.root
),
executor = if (systemBuildInfo.sdk >= 28) {
accessibilityExecutorFactory(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
            } else {
                rootExecutorFactory("input keyevent 26")
            }
        ) as @StringKey("lock_screen") Action
    }
}
 */