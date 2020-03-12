package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PowerSettingsNew
import com.ivianuu.essentials.gestures.R
import com.ivianuu.injekt.Module

internal val EsPowerDialogActionModule = Module {
    bindAccessibilityAction(
        key = "power_dialog",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
        titleRes = R.string.es_action_power_dialog,
        icon = { Icon(Icons.Default.PowerSettingsNew) }
    )
}
