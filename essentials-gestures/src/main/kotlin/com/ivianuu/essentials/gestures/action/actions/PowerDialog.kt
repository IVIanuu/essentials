package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PowerSettingsNew
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun PowerDialogModule() {
    installIn<ApplicationComponent>()
    bindAccessibilityAction<@StringKey("power_dialog") Action>(
        key = "power_dialog",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
        titleRes = R.string.es_action_power_dialog,
        icon = { Icon(Icons.Default.PowerSettingsNew) }
    )
}
