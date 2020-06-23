package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun QuickSettingsModule() {
    installIn<ApplicationComponent>()
    bindAccessibilityAction<@StringKey("quick_settings") Action>(
        key = "quick_settings",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
        titleRes = R.string.es_action_quick_settings,
        icon = { Icon(Icons.Default.Settings) }
    )
}
