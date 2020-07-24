package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PowerSettingsNew
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.Reader

@BindAction
@Reader
fun powerDialogAction() = accessibilityAction(
    key = "power_dialog",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
    titleRes = R.string.es_action_power_dialog,
    icon = singleActionIcon(Icons.Default.PowerSettingsNew)
)
