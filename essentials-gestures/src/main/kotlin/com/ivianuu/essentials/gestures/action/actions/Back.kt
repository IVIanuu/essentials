package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements

@BindAction
@Reader
fun backAction() = accessibilityAction(
    key = "back",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_BACK,
    titleRes = R.string.es_action_back,
    icon = { Icon(vectorResource(R.drawable.es_ic_action_back)) }
)
