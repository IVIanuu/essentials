package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun recentAppsAction(accessibilityAction: accessibilityAction): Action = accessibilityAction(
    "recent_apps",
    AccessibilityService.GLOBAL_ACTION_RECENTS,
    R.string.es_action_recent_apps,
    singleActionIcon(R.drawable.es_ic_action_recent_apps)
)
