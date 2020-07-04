package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun SearchModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executorFactory: @Provider (Intent) -> IntentActionExecutor ->
        Action(
key = "search",
title = getString(R.string.es_action_search),
iconProvider = SingleActionIconProvider(Icons.Default.Search),
            executor = executorFactory(
                Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(
                        "com.google.android.googlequicksearchbox",
                        "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        ) as @StringKey("search") Action
    }
}
 */