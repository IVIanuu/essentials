package com.ivianuu.essentials.systemoverlay.runner

import android.view.Gravity
import androidx.compose.ui.Alignment
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.systemoverlay.R

enum class TriggerPosition {
    LEFT, TOP, RIGHT, BOTTOM;
}

val TriggerPosition.alignment: Alignment
    get() = when (this) {
        TriggerPosition.LEFT -> Alignment.TopStart
        TriggerPosition.TOP -> Alignment.TopStart
        TriggerPosition.RIGHT -> Alignment.TopEnd
        TriggerPosition.BOTTOM -> Alignment.BottomStart
    }

val TriggerPosition.iconRes: Int
    get() = when (this) {
        TriggerPosition.LEFT -> R.drawable.es_ic_trigger_left
        TriggerPosition.TOP -> R.drawable.es_ic_trigger_top
        TriggerPosition.RIGHT -> R.drawable.es_ic_trigger_right
        TriggerPosition.BOTTOM -> R.drawable.es_ic_trigger_bottom
    }

val TriggerPosition.gravityInt: Int
    get() = when (this) {
        TriggerPosition.LEFT -> Gravity.LEFT or Gravity.TOP
        TriggerPosition.TOP -> Gravity.TOP or Gravity.LEFT
        TriggerPosition.RIGHT -> Gravity.RIGHT or Gravity.TOP
        TriggerPosition.BOTTOM -> Gravity.BOTTOM or Gravity.LEFT
    }

val TriggerPosition.isOnSide: Boolean
    get() = this == TriggerPosition.LEFT || this == TriggerPosition.RIGHT

fun TriggerPosition.rotate(rotation: DisplayRotation): TriggerPosition = when (this) {
    TriggerPosition.LEFT -> when (rotation) {
        DisplayRotation.PORTRAIT_UP -> this
        DisplayRotation.LANDSCAPE_LEFT -> TriggerPosition.BOTTOM
        DisplayRotation.PORTRAIT_DOWN -> TriggerPosition.RIGHT
        DisplayRotation.LANDSCAPE_RIGHT -> TriggerPosition.TOP
    }
    TriggerPosition.TOP -> when (rotation) {
        DisplayRotation.PORTRAIT_UP -> this
        DisplayRotation.LANDSCAPE_LEFT -> TriggerPosition.LEFT
        DisplayRotation.PORTRAIT_DOWN -> TriggerPosition.BOTTOM
        DisplayRotation.LANDSCAPE_RIGHT -> TriggerPosition.RIGHT
    }
    TriggerPosition.RIGHT -> when (rotation) {
        DisplayRotation.PORTRAIT_UP -> this
        DisplayRotation.LANDSCAPE_LEFT -> TriggerPosition.TOP
        DisplayRotation.PORTRAIT_DOWN -> TriggerPosition.LEFT
        DisplayRotation.LANDSCAPE_RIGHT -> TriggerPosition.BOTTOM
    }
    TriggerPosition.BOTTOM -> when (rotation) {
        DisplayRotation.PORTRAIT_UP -> this
        DisplayRotation.LANDSCAPE_LEFT -> TriggerPosition.RIGHT
        DisplayRotation.PORTRAIT_DOWN -> TriggerPosition.TOP
        DisplayRotation.LANDSCAPE_RIGHT -> TriggerPosition.LEFT
    }
}
