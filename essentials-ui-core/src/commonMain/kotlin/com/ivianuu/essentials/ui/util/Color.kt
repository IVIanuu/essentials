/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.util

import androidx.compose.ui.graphics.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*

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
  catch { toHexString() }.getOrElse { null }

expect fun String.toColor(): Color

fun String.toColorOrNull(): Color? = catch { toColor() }.getOrElse { null }
