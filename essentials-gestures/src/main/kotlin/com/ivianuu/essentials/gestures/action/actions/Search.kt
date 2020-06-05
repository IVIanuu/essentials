package com.ivianuu.essentials.gestures.action.actions

import android.content.ComponentName
import android.content.Intent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Search
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun SearchModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executorFactory: @Provider (Intent) -> IntentActionExecutor ->
        Action(
            key = "search",
            title = resourceProvider.getString(R.string.es_action_search),
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
