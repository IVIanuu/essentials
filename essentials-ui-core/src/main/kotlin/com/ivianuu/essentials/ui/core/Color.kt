/*
 * Copyright 2020 Manuel Wrage
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

fun Color.blend(blendColor: Color): Color {
    return Color(
        red = blendColor.red * blendColor.alpha + red * (1 - blendColor.alpha),
        green = blendColor.green * blendColor.alpha + green * (1 - blendColor.alpha),
        blue = blendColor.blue * blendColor.alpha + blue * (1 - blendColor.alpha)
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

fun Color.toHexStringOrNull(includeAlpha: Boolean = true) = try {
    toHexString(includeAlpha)
} catch (e: Throwable) {
    null
}

fun String.toColor(): Color {
    val finalColorString = if (startsWith("#")) this else "#$this"
    return Color(android.graphics.Color.parseColor(finalColorString))
}

fun String.toColorOrNull(): Color? = try {
    toColor()
} catch (e: Throwable) {
    null
}
