package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Notifications
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun NotificationsModule() {
    installIn<ApplicationComponent>()
    bindAccessibilityAction<@StringKey("notifications") Action>(
        key = "notifications",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
        titleRes = R.string.es_action_notifications,
        icon = { Icon(Icons.Default.Notifications) }
    )
}
