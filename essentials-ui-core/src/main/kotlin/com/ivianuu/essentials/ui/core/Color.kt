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

package com.ivianuu.essentials.ui.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull

val Color.isDark: Boolean
  get() = ColorUtils.calculateLuminance(toArgb()) < 0.5

val Color.isLight: Boolean
  get() = !isDark

fun Color.shifted(by: Float): Color {
  if (by == 1f) return this
  val alpha = (alpha * 255).toInt()
  val hsv = FloatArray(3)
  android.graphics.Color.colorToHSV(toArgb(), hsv)
  hsv[2] *= by
  return Color((alpha shl 24) + (0x00ffffff and android.graphics.Color.HSVToColor(hsv)))
}

fun Color.darken(): Color = shifted(0.9f)

fun Color.lighten(): Color = shifted(1.1f)

fun Color.blend(other: Color): Color {
  val thisArgb = toArgb()
  val otherArgb = other.toArgb()
  val alphaChannel = 24
  val redChannel = 16
  val greenChannel = 8
  val blueChannel = 0
  val ap1 = (thisArgb shr alphaChannel and 0xff).toDouble() / 255.0
  val ap2 = (otherArgb shr alphaChannel and 0xff).toDouble() / 255.0
  val ap = ap2 + ap1 * (1 - ap2)
  val amount1 = ap1 * (1 - ap2) / ap
  val amount2 = amount1 / ap
  val a = (ap * 255.0).toInt() and 0xff
  val r = ((thisArgb shr redChannel and 0xff).toFloat() * amount1 +
      (otherArgb shr redChannel and 0xff).toFloat() * amount2).toInt() and 0xff
  val g = ((thisArgb shr greenChannel and 0xff).toFloat() * amount1 +
      (otherArgb shr greenChannel and 0xff).toFloat() * amount2).toInt() and 0xff
  val b = ((thisArgb and 0xff).toFloat() * amount1 +
      (otherArgb and 0xff).toFloat() * amount2).toInt() and 0xff
  return Color(
    a shl alphaChannel or
        (r shl redChannel) or
        (g shl greenChannel) or
        (b shl blueChannel)
  )
}

fun Color.inverted(): Color = Color(
  alpha = alpha,
  red = 255 - red,
  green = 255 - green,
  blue = 255 - blue
)

fun Color.toHexString(includeAlpha: Boolean = true) = if (this.toArgb() == 0) {
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

fun Color.toHexStringOrNull(includeAlpha: Boolean = true) =
  catch { toHexString(includeAlpha) }.getOrNull()

fun String.toColor(): Color {
  val finalColorString = if (startsWith("#")) this else "#$this"
  return Color(android.graphics.Color.parseColor(finalColorString))
}

fun String.toColorOrNull(): Color? = catch { toColor() }.getOrNull()
