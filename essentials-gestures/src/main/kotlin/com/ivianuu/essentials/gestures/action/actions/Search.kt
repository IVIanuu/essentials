package com.ivianuu.essentials.gestures.action.actions

import android.content.ComponentName
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.util.Resources

@ActionBinding
fun searchAction(
    resources: Resources,
    sendIntent: sendIntent,
): Action = Action(
    key = "search",
    title = resources.getString(R.string.es_action_search),
    icon = singleActionIcon(Icons.Default.Search),
    execute = {
        sendIntent(
            Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(
                    "com.google.android.googlequicksearchbox",
                    "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
)
