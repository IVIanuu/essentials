package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun powerDialogAction() = accessibilityAction(
    key = "power_dialog",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
    titleRes = R.string.es_action_power_dialog,
    icon = singleActionIcon(R.drawable.es_ic_power_settings_new)
)
