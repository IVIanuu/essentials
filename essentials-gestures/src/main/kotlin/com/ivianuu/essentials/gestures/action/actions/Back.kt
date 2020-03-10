package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.path
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import com.ivianuu.injekt.Module

val EssentialsIcons.ActionBack: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(18.5f, 19.03f)
        verticalLineTo(4.97f)
        lineTo(6.53f, 12.0f)
        lineToRelative(11.97f, 7.03f)
        close()
        moveTo(3.54f, 10.28f)
        lineToRelative(14.97f, -8.8f)
        curveToRelative(1.4f, -0.82f, 2.99f, 0.06f, 2.99f, 1.7f)
        verticalLineToRelative(17.65f)
        curveToRelative(0.0f, 1.63f, -1.6f, 2.51f, -2.99f, 1.7f)
        lineToRelative(-14.97f, -8.8f)
        arcToRelative(1.94f, 1.94f, 0.0f, false, true, 0.0f, -3.45f)
        close()
    }
}

internal val EsBackActionModule = Module {
    bindAccessibilityAction(
        key = "back",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_BACK,
        titleRes = R.string.es_action_back,
        icon = Icons.Essentials.ActionBack
    )
}