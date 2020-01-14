package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsBackActionModule = Module {
    bindAccessibilityAction(
        key = "back",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_BACK,
        titleRes = R.string.es_action_back,
        iconRes = R.drawable.es_ic_action_back
    )
}
