package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ViewAgenda
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun splitScreenAction() = accessibilityAction(
    key = "split_screen",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN,
    titleRes = R.string.es_action_split_screen,
    icon = singleActionIcon(Icons.Default.ViewAgenda)
)
