package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun notificationsAction(accessibilityAction: accessibilityAction) = accessibilityAction(
    "notifications",
    AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
    R.string.es_action_notifications,
    singleActionIcon(Icons.Default.Notifications)
)
