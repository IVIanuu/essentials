package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PowerSettingsNew
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.esPowerDialogActionBindings() {
    bindAccessibilityAction(
        key = "power_dialog",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
        titleRes = R.string.es_action_power_dialog,
        icon = Icons.Default.PowerSettingsNew
    )
}
