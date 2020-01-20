package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.os.Build
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsSplitScreenActionModule = Module {
    if (Build.VERSION.SDK_INT >= 24) {
        bindAccessibilityAction(
            key = "split_screen",
            accessibilityAction = AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN,
            titleRes = R.string.es_action_split_screen,
            iconRes = R.drawable.es_ic_power_settings
        )
    }
}
