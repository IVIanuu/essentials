package com.ivianuu.essentials.gestures.action.actions

import android.content.ComponentName
import android.content.Intent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Search
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf

@ApplicationScope
@Module
private fun ComponentBuilder.searchAction() {
    action(
        key = "search",
        title = { getStringResource(R.string.es_action_search) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Search) },
        unlockScreen = { true },
        executor = {
            get<IntentActionExecutor>(
                parameters = parametersOf(
                    Intent(Intent.ACTION_MAIN).apply {
                        component = ComponentName(
                            "com.google.android.googlequicksearchbox",
                            "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            )
        }
    )
}
