package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun backAction(
    accessibilityAction: accessibilityAction,
): Action = accessibilityAction(
    "back",
    AccessibilityService.GLOBAL_ACTION_BACK,
    R.string.es_action_back,
    singleActionIcon(R.drawable.es_ic_action_back)
)
