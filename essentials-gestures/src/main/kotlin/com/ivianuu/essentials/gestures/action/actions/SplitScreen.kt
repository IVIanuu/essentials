package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ViewAgenda
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.splitScreenAction() {
    bindAccessibilityAction(
        key = "split_screen",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN,
        titleRes = R.string.es_action_split_screen,
        icon = { Icon(Icons.Default.ViewAgenda) }
    )
}
