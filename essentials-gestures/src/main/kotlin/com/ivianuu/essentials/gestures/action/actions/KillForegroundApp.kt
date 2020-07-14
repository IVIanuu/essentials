package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun KillForegroundAppModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             executor: KillForegroundAppActionExecutor ->
        Action(
key = "kill_foreground_action",
title = getString(R.string.es_action_kill_foreground_app),
iconProvider = SingleActionIconProvider(Icons.Default.Clear),
            permissions = listOf(
                permissions.accessibility,
                permissions.root
            ),
            executor = executor
        ) as @StringKey("kill_foreground_action") Action
    }
}

@Given
internal class KillForegroundAppActionExecutor(
    private val buildInfo: BuildInfo,
    private val recentAppsProvider: RecentAppsProvider,
private val packageManager: PackageManager,
private val rootActionExecutorProvider: (String) -> RootActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        val currentApp = recentAppsProvider.currentApp.first()

        if (currentApp != "android" &&
            currentApp != "com.android.systemui" &&
            currentApp != buildInfo.packageName && // we have no suicidal intentions :D
            currentApp != getHomePackage()
        ) {
            rootActionExecutorProvider("am force-stop $currentApp")()
        }
    }

    private fun getHomePackage(): String {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        return packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName ?: ""
    }

}
 */