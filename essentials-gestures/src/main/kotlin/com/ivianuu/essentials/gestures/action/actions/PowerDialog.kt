package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun powerDialogAction(accessibilityAction: accessibilityAction) = accessibilityAction(
    "power_dialog",
    AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
    R.string.es_action_power_dialog,
    singleActionIcon(R.drawable.es_ic_power_settings_new)
)
