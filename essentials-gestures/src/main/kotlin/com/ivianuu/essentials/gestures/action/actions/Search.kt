package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn

@Module
private fun SearchModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("search") Action>(
        key = "search",
        title = { getStringResource(R.string.es_action_search) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Search) },
        unlockScreen = { true },
        executor = {
            get<@Provider (Intent) -> IntentActionExecutor>()(
                Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(
                        "com.google.android.googlequicksearchbox",
                        "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    )*/
}
