package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Notifications
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.notificationsAction() {
    bindAccessibilityAction(
        key = "notifications",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
        titleRes = R.string.es_action_notifications,
        icon = { Icon(Icons.Default.Notifications) }
    )
}
