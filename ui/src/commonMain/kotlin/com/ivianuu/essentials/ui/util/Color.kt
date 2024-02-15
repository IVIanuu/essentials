/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import arrow.core.Either
import com.ivianuu.essentials.serialization.InjektSerializer
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
  Either.catch { toHexString() }.getOrNull()

expect fun String.toColor(): Color

fun String.toColorOrNull(): Color? = Either.catch { toColor() }.getOrNull()

@Provide @InjektSerializer object ColorSerializer : KSerializer<Color> {
  override val descriptor =
    PrimitiveSerialDescriptor("androidx.compose.ui.graphics.Color", PrimitiveKind.INT)

  override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.toArgb())

  override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())
}
