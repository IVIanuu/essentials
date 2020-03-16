package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PowerSettingsNew
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.powerDialogAction() {
    bindAccessibilityAction(
        key = "power_dialog",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
        titleRes = R.string.es_action_power_dialog,
        icon = { Icon(Icons.Default.PowerSettingsNew) }
    )
}
