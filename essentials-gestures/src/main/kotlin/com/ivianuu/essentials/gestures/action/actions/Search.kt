package com.ivianuu.essentials.gestures.action.actions

/**
import android.content.ComponentName
import android.content.Intent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.data.Flag

private fun createSearchAction() = Action(
    key = KEY_SEARCH,
    title = string(R.string.action_search),
    states = stateless(R.drawable.es_ic_search),
    flags = setOf(Flag.UnlockScreen)
)

private fun openSearch() {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        component = ComponentName(
            "com.google.android.googlequicksearchbox",
            "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
        )
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
*/