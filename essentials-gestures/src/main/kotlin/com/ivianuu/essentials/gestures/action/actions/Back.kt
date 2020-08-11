package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun backAction() = accessibilityAction(
    key = "back",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_BACK,
    titleRes = R.string.es_action_back,
    icon = singleActionIcon(R.drawable.es_ic_action_back)
)
