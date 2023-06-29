package com.ivianuu.essentials.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.shiftColor(by: Float): Color {
  if (by == 1f) return this
  val alpha = (alpha * 255f).toInt()
  val hsv = FloatArray(3)
  android.graphics.Color.colorToHSV(toArgb(), hsv)
  hsv[2] *= by
  return Color((alpha shl 24) + (0x00ffffff and android.graphics.Color.HSVToColor(hsv)))
}
