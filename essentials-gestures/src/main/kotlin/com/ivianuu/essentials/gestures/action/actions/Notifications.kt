package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsNotificationsActionModule = Module {
    bindAccessibilityAction(
        key = "notifications",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
        titleRes = R.string.es_action_notifications,
        iconRes = R.drawable.es_ic_notifications
    )
}
