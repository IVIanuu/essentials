package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.Reader

@BindAction
@Reader
fun quickSettingsAction() = accessibilityAction(
    key = "quick_settings",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
    titleRes = R.string.es_action_quick_settings,
    icon = singleActionIcon(Icons.Default.Settings)
)