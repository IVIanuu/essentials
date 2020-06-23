package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.foundation.Icon
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun BackModule() {
    installIn<ApplicationComponent>()
    bindAccessibilityAction<@StringKey("back") Action>(
        key = "back",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_BACK,
        titleRes = R.string.es_action_back,
        icon = { Icon(vectorResource(R.drawable.es_ic_action_back)) }
    )
}
