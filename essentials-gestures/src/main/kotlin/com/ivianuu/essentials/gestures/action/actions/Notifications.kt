package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Notifications
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsNotificationsActionModule = Module {
    bindAccessibilityAction(
        key = "notifications",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
        titleRes = R.string.es_action_notifications,
        icon = Icons.Default.Notifications
    )
}
