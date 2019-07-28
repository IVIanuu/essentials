package com.ivianuu.essentials.ui.compose.view

import android.view.Gravity
import android.view.View
import androidx.ui.layout.Alignment

fun viewId() = View.generateViewId()

fun Alignment.toGravityInt() = when (this) {
    Alignment.TopLeft -> Gravity.TOP or Gravity.START
    Alignment.TopCenter -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
    Alignment.TopRight -> Gravity.TOP or Gravity.END
    Alignment.CenterLeft -> Gravity.START or Gravity.CENTER_VERTICAL
    Alignment.Center -> Gravity.CENTER
    Alignment.CenterRight -> Gravity.END or Gravity.CENTER_VERTICAL
    Alignment.BottomLeft -> Gravity.BOTTOM or Gravity.START
    Alignment.BottomCenter -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    Alignment.BottomRight -> Gravity.BOTTOM or Gravity.END
}