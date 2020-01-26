package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.injekt.Module

val EssentialsIcons.Settings: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(19.43f, 12.98f)
        curveToRelative(0.04f, -0.32f, 0.07f, -0.64f, 0.07f, -0.98f)
        reflectiveCurveToRelative(-0.03f, -0.66f, -0.07f, -0.98f)
        lineToRelative(2.11f, -1.65f)
        curveToRelative(0.19f, -0.15f, 0.24f, -0.42f, 0.12f, -0.64f)
        lineToRelative(-2.0f, -3.46f)
        curveToRelative(-0.12f, -0.22f, -0.39f, -0.3f, -0.61f, -0.22f)
        lineToRelative(-2.49f, 1.0f)
        curveToRelative(-0.52f, -0.4f, -1.08f, -0.73f, -1.69f, -0.98f)
        lineToRelative(-0.38f, -2.65f)
        curveTo(14.46f, 2.18f, 14.25f, 2.0f, 14.0f, 2.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(-0.25f, 0.0f, -0.46f, 0.18f, -0.49f, 0.42f)
        lineToRelative(-0.38f, 2.65f)
        curveToRelative(-0.61f, 0.25f, -1.17f, 0.59f, -1.69f, 0.98f)
        lineToRelative(-2.49f, -1.0f)
        curveToRelative(-0.23f, -0.09f, -0.49f, 0.0f, -0.61f, 0.22f)
        lineToRelative(-2.0f, 3.46f)
        curveToRelative(-0.13f, 0.22f, -0.07f, 0.49f, 0.12f, 0.64f)
        lineToRelative(2.11f, 1.65f)
        curveToRelative(-0.04f, 0.32f, -0.07f, 0.65f, -0.07f, 0.98f)
        reflectiveCurveToRelative(0.03f, 0.66f, 0.07f, 0.98f)
        lineToRelative(-2.11f, 1.65f)
        curveToRelative(-0.19f, 0.15f, -0.24f, 0.42f, -0.12f, 0.64f)
        lineToRelative(2.0f, 3.46f)
        curveToRelative(0.12f, 0.22f, 0.39f, 0.3f, 0.61f, 0.22f)
        lineToRelative(2.49f, -1.0f)
        curveToRelative(0.52f, 0.4f, 1.08f, 0.73f, 1.69f, 0.98f)
        lineToRelative(0.38f, 2.65f)
        curveToRelative(0.03f, 0.24f, 0.24f, 0.42f, 0.49f, 0.42f)
        horizontalLineToRelative(4.0f)
        curveToRelative(0.25f, 0.0f, 0.46f, -0.18f, 0.49f, -0.42f)
        lineToRelative(0.38f, -2.65f)
        curveToRelative(0.61f, -0.25f, 1.17f, -0.59f, 1.69f, -0.98f)
        lineToRelative(2.49f, 1.0f)
        curveToRelative(0.23f, 0.09f, 0.49f, 0.0f, 0.61f, -0.22f)
        lineToRelative(2.0f, -3.46f)
        curveToRelative(0.12f, -0.22f, 0.07f, -0.49f, -0.12f, -0.64f)
        lineToRelative(-2.11f, -1.65f)
        close()
        moveTo(12.0f, 15.5f)
        curveToRelative(-1.93f, 0.0f, -3.5f, -1.57f, -3.5f, -3.5f)
        reflectiveCurveToRelative(1.57f, -3.5f, 3.5f, -3.5f)
        reflectiveCurveToRelative(3.5f, 1.57f, 3.5f, 3.5f)
        reflectiveCurveToRelative(-1.57f, 3.5f, -3.5f, 3.5f)
        close()
    }
}

internal val EsQuickSettingsActionModule = Module {
    bindAccessibilityAction(
        key = "quick_settings",
        accessibilityAction = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS,
        titleRes = R.string.es_action_quick_settings,
        icon = Icons.Essentials.Settings
    )
}

