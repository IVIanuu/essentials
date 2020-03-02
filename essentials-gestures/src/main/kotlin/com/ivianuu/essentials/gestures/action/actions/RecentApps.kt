package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.lazyMaterialIcon
import androidx.ui.graphics.vector.path
import com.ivianuu.injekt.Module

val EssentialsIcons.ActionRecentApps: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.67f, 18.67f)
        verticalLineTo(5.33f)
        horizontalLineTo(5.33f)
        verticalLineToRelative(13.34f)
        horizontalLineToRelative(13.34f)
        close()
        moveToRelative(-14.0f, 3.0f)
        arcToRelative(2.33f, 2.33f, 0.0f, false, true, -2.34f, -2.34f)
        verticalLineTo(4.67f)
        arcToRelative(2.33f, 2.33f, 0.0f, false, true, 2.34f, -2.34f)
        horizontalLineToRelative(14.66f)
        arcToRelative(2.33f, 2.33f, 0.0f, false, true, 2.34f, 2.34f)
        verticalLineToRelative(14.66f)
        arcToRelative(2.33f, 2.33f, 0.0f, false, true, -2.34f, 2.34f)
        horizontalLineTo(4.67f)
        close()
    }
}

internal val EsRecentsAppsActionModule = Module {
    bindAccessibilityAction(
        key = "recent_apps",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_RECENTS,
        titleRes = R.string.es_action_recent_apps,
        icon = Icons.Essentials.ActionRecentApps
    )
}
