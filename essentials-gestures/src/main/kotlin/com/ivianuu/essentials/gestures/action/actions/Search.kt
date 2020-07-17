package com.ivianuu.essentials.gestures.action.actions

import android.content.ComponentName
import android.content.Intent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Search
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@BindAction
@Reader
fun searchAction() = Action(
    key = "search",
    title = Resources.getString(R.string.es_action_search),
    iconProvider = SingleActionIconProvider(Icons.Default.Search),
    executor = given<(Intent) -> IntentActionExecutor>()(
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.google.android.googlequicksearchbox",
                "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
)
