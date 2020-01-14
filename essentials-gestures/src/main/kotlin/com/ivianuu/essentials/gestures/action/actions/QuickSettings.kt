package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsQuickSettingsActionModule = Module {
    bindAccessibilityAction(
        key = "quick_settings",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
        titleRes = R.string.es_action_quick_settings,
        iconRes = R.drawable.es_ic_settings
    )
}
