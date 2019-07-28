package com.ivianuu.essentials.ui.compose.view

import android.view.Gravity
import androidx.ui.layout.Alignment

fun Alignment.toGravity() = when (this) {
    Alignment.TopLeft -> Gravity.TOP or Gravity.LEFT
    Alignment.TopCenter -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
    Alignment.TopRight -> Gravity.TOP or Gravity.RIGHT
    Alignment.CenterLeft -> Gravity.LEFT or Gravity.CENTER_VERTICAL
    Alignment.Center -> Gravity.CENTER
    Alignment.CenterRight -> Gravity.RIGHT or Gravity.CENTER_VERTICAL
    Alignment.BottomLeft -> Gravity.BOTTOM or Gravity.LEFT
    Alignment.BottomCenter -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    Alignment.BottomRight -> Gravity.BOTTOM or Gravity.RIGHT
}