/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  var color: Long = finalColorString.substring(1).toLong(16)
  if (length == 7) {
    // Set the alpha value
    color = color or -0x1000000
  } else require(length == 9) { "Unknown color" }
  return Color(color.toInt())
}
