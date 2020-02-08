package com.ivianuu.essentials.gestures.action.actions

import android.content.ComponentName
import android.content.Intent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.Search
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf

internal val EsSearchActionModule = Module {
    bindAction(
        key = "search",
        title = { getStringResource(R.string.es_action_search) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Search) },
        unlockScreen = { true },
        executor = {
            get<IntentActionExecutor>(
                parametersOf(
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
