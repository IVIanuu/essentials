package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Notifications
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.esNotificationsActionBindings() {
    bindAccessibilityAction(
        key = "notifications",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
        titleRes = R.string.es_action_notifications,
        icon = Icons.Default.Notifications
    )
}
