package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun RecentAppsModule() {
    installIn<ApplicationComponent>()
    bindAccessibilityAction<@StringKey("recent_apps") Action>(
        key = "recent_apps",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_RECENTS,
        titleRes = R.string.es_action_recent_apps,
        icon = { Icon(vectorResource(R.drawable.es_ic_action_recent_apps)) }
    )
}
