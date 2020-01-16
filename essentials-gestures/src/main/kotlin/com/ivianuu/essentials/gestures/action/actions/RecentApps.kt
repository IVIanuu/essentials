package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsRecentsAppsActionModule = Module {
    bindAccessibilityAction(
        key = "recent_apps",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_RECENTS,
        titleRes = R.string.es_action_recent_apps,
        iconRes = R.drawable.es_ic_action_recent_apps
    )
}
