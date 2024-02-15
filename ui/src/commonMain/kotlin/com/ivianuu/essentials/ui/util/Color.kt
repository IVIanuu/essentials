/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.util

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import arrow.core.Either
import com.ivianuu.essentials.Lerper
import com.ivianuu.essentials.serialization.InjektSerializer
import com.ivianuu.essentials.unlerp
import com.ivianuu.injekt.Provide
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

val Color.isDark: Boolean get() = !isLight

val Color.isLight: Boolean get() = luminance() > 0.5f

expect fun Color.toHexString(includeAlpha: Boolean = true): String

fun Color.toHexStringOrNull(includeAlpha: Boolean = true): String? =
  Either.catch { toHexString(includeAlpha) }.getOrNull()

expect fun String.toColor(): Color

fun String.toColorOrNull(): Color? = Either.catch { toColor() }.getOrNull()

@Provide @InjektSerializer object ColorSerializer : KSerializer<Color> {
  override val descriptor =
    PrimitiveSerialDescriptor("androidx.compose.ui.graphics.Color", PrimitiveKind.INT)

  override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.toArgb())

  override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())
}

@Provide val ColorsLerper = Lerper<Colors>(
  lerp = { start, stop, fraction ->
    Colors(
      isLight = if (fraction < 0.5) start.isLight else stop.isLight,
      primary = lerp(start.primary, stop.primary, fraction),
      primaryVariant = lerp(start.primaryVariant, stop.primaryVariant, fraction),
      secondary = lerp(start.secondary, stop.secondary, fraction),
      secondaryVariant = lerp(start.secondaryVariant, stop.secondaryVariant, fraction),
      background = lerp(start.background, stop.background, fraction),
      surface = lerp(start.surface, stop.surface, fraction),
      error = lerp(start.error, stop.error, fraction),
      onPrimary = lerp(start.onPrimary, stop.onPrimary, fraction),
      onSecondary = lerp(start.onSecondary, stop.onSecondary, fraction),
      onBackground = lerp(start.onBackground, stop.onBackground, fraction),
      onSurface = lerp(start.onSurface, stop.onSurface, fraction),
      onError = lerp(start.onError, stop.onError, fraction)
    )
  },
  unlerp = { start, stop, current ->
    unlerp(start.primary.alpha, stop.primary.alpha, current.primary.alpha)
  }
)

fun colors(
  isLight: Boolean = true,
  primary: Color = if (isLight) Color(0xFF6200EE) else Color(0xFFBB86FC),
  primaryVariant: Color = if (isLight) Color(0xFF3700B3) else Color(0xFF3700B3),
  secondary: Color = if (isLight) Color(0xFF03DAC6) else Color(0xFF03DAC6),
  secondaryVariant: Color = if (isLight) Color(0xFF018786) else Color(0xFF03DAC6),
  background: Color = if (isLight) Color.White else Color(0xFF121212),
  surface: Color = if (isLight) Color.White else Color(0xFF121212),
  error: Color = if (isLight) Color(0xFFB00020) else Color(0xFFCF6679),
  onPrimary: Color = if (primary.isDark) Color.White else Color.Black,
  onSecondary: Color = if (secondary.isDark) Color.White else Color.Black,
  onBackground: Color = if (background.isDark) Color.White else Color.Black,
  onSurface: Color = if (surface.isDark) Color.White else Color.Black,
  onError: Color = if (error.isDark) Color.White else Color.Black,
) = if (isLight) {
  lightColors(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
  )
} else {
  darkColors(
    primary, primaryVariant,
    secondary = secondary,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
  )
}
