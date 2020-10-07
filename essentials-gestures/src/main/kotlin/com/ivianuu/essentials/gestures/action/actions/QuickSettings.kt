package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun quickSettingsAction(accessibilityAction: accessibilityAction) = accessibilityAction(
    "quick_settings",
    AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
    R.string.es_action_quick_settings,
    singleActionIcon(Icons.Default.Settings)
)