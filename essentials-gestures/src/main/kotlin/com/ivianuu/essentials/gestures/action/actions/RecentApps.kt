package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.Reader

@BindAction
@Reader
fun recentAppsAction() = accessibilityAction(
    key = "recent_apps",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_RECENTS,
    titleRes = R.string.es_action_recent_apps,
    icon = { Icon(vectorResource(R.drawable.es_ic_action_recent_apps)) }
)
