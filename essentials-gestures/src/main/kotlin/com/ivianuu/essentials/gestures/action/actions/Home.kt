package com.ivianuu.essentials.gestures.action.actions

/**
private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"

@Module
fun HomeModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             intentHomeExecutorFactory: @Provider () -> IntentHomeActionExecutor,
             accessibilityExecutorFactory: @Provider (Int) -> AccessibilityActionExecutor ->
        Action(
key = "home",
title = getString(R.string.es_action_home),
permissions = if (needsHomeIntentWorkaround) emptyList()
            else listOf(permissions.accessibility),
            iconProvider = SingleActionIconProvider(R.drawable.es_ic_action_home),
            executor = if (needsHomeIntentWorkaround) intentHomeExecutorFactory()
            else accessibilityExecutorFactory(AccessibilityService.GLOBAL_ACTION_HOME)
        ) as @StringKey("home") Action
    }
}

@Unscoped
internal class IntentHomeActionExecutor(
    private val context: @ForApplication Context,
    private val delegateProvider: @Provider (Intent) -> IntentActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        delegateProvider(Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) })()
    }
}
 */