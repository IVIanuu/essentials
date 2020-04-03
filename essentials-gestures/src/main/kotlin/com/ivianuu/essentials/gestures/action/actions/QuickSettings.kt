package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.quickSettingsAction() {
    bindAccessibilityAction(
        key = "quick_settings",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
        titleRes = R.string.es_action_quick_settings,
        icon = { Icon(Icons.Default.Settings) }
    )
}

