package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun recentAppsAction() = accessibilityAction(
    key = "recent_apps",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_RECENTS,
    titleRes = R.string.es_action_recent_apps,
    icon = singleActionIcon(R.drawable.es_ic_action_recent_apps)
)
