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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull

val Color.isDark: Boolean get() = !isLight

val Color.isLight: Boolean get() = luminance() > 0.5f

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

expect fun Color.toHexString(includeAlpha: Boolean = true): String

fun Color.toHexStringOrNull(includeAlpha: Boolean = true): String? =
  catch { toHexString() }.getOrNull()

expect fun String.toColor(): Color

fun String.toColorOrNull(): Color? = catch { toColor() }.getOrNull()
