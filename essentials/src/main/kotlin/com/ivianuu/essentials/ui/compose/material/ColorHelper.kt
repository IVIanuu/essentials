/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import androidx.ui.material.Colors
import androidx.ui.material.surface.CurrentBackground
import com.ivianuu.essentials.util.isDark

fun colorForCurrentBackground() = effectOf<Color> {
    +colorForBackground(+ambient(CurrentBackground))
}

fun colorForBackground(color: Color) = effectOf<Color> {
    var result = with(+ambient(Colors)) {
        when (color) {
            primary -> onPrimary
            primaryVariant -> onPrimary
            secondary -> onSecondary
            secondaryVariant -> onSecondary
            background -> onBackground
            surface -> onSurface
            error -> onError
            else -> null
        }
    }
    if (result == null) {
        result = if (color.toArgb().isDark) OnDarkColor else OnLightColor
    }
    return@effectOf result
}

val PrimaryTextAlpha = 0.87f
val PrimaryTextDisabledAlpha = 0.38f

val SecondaryTextAlpha = 0.60f
val SecondaryTextDisabledAlpha = 0.24f // todo is not material spec

val ActiveIconAlpha = 0.87f
val ActiveIconDisabledAlpha = 0.38f

val InactiveIconAlpha = 0.60f
val InactiveIconDisabledAlpha = 0.24f // todo is not material spec

val OnLightColor = Color.Black
val OnDarkColor = Color.White