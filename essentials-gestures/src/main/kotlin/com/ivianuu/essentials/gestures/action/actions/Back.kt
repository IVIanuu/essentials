package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.backAction() {
    bindAccessibilityAction(
        key = "back",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_BACK,
        titleRes = R.string.es_action_back,
        icon = { Icon(vectorResource(R.drawable.es_ic_action_back)) }
    )
}