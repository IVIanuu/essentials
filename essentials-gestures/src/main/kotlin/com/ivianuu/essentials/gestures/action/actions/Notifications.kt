package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.Reader

@BindAction
@Reader
fun notificationsAction() = accessibilityAction(
    key = "notifications",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
    titleRes = R.string.es_action_notifications,
    icon = singleActionIcon(Icons.Default.Notifications)
)
