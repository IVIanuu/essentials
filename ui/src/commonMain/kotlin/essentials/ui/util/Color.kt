/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.util

import androidx.compose.ui.graphics.*
import essentials.catch
import essentials.serialization.*
import injekt.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

val Color.isDark: Boolean get() = !isLight

val Color.isLight: Boolean get() = luminance() > 0.5f

//expect fun Color.toHexString(includeAlpha: Boolean = true): String

fun Color.toHexStringOrNull(includeAlpha: Boolean = true): String? =
  catch { toHexString(includeAlpha) }.getOrNull()

//expect fun String.toColor(): Color

fun String.toColorOrNull(): Color? = catch { toColor() }.getOrNull()

@Provide @InjektSerializer object ColorSerializer : KSerializer<Color> {
  override val descriptor =
    PrimitiveSerialDescriptor("androidx.compose.ui.graphics.Color", PrimitiveKind.INT)

  override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.toArgb())

  override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())
}
