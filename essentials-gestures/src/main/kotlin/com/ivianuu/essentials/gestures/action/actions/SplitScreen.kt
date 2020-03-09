package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.os.Build
import com.ivianuu.essentials.gestures.R
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PowerSettingsNew
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.esSplitScreenActionBindings() {
    if (Build.VERSION.SDK_INT >= 24) {
        bindAccessibilityAction(
            key = "split_screen",
            accessibilityAction = AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN,
            titleRes = R.string.es_action_split_screen,
            icon = Icons.Default.PowerSettingsNew
        )
    }
}
