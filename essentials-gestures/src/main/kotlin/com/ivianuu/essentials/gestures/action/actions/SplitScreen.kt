package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun splitScreenAction(accessibilityAction: accessibilityAction) = accessibilityAction(
    "split_screen",
    AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN,
    R.string.es_action_split_screen,
    singleActionIcon(R.drawable.es_ic_view_agenda)
)
