package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ViewAgenda
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.Reader

@BindAction
@Reader
fun splitScreenAction() = accessibilityAction(
    key = "split_screen",
    accessibilityAction = AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN,
    titleRes = R.string.es_action_split_screen,
    icon = { Icon(Icons.Default.ViewAgenda) }
)
