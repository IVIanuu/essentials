/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

actual fun Color.toHexString(includeAlpha: Boolean) = if (this.toArgb() == 0) {
  if (includeAlpha) "00000000" else "000000"
} else {
  if (includeAlpha) {
    val result = Integer.toHexString(this.toArgb())
    if (result.length == 6) {
      "00$result"
    } else {
      result
    }
  } else {
    String.format("%06X", 0xFFFFFF and this.toArgb())
  }
}

actual fun String.toColor(): Color {
  val finalColorString = if (startsWith("#")) this else "#$this"
  var color = finalColorString.substring(1).toLong(16)
  if (finalColorString.length == 7) {
    // Set the alpha value
    color = color or -0x1000000
  } else require(finalColorString.length == 9) { "Unknown color" }
  return Color(color.toInt())
}
