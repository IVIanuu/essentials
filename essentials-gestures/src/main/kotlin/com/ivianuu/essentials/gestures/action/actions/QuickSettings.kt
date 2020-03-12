package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.quickSettingsAction() {
    bindAccessibilityAction(
        key = "quick_settings",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
        titleRes = R.string.es_action_quick_settings,
        icon = { Icon(Icons.Default.Settings) }
    )
}

