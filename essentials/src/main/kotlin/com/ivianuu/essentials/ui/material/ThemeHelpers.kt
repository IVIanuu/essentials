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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.ripple.DefaultRippleEffectFactory
import androidx.ui.material.ripple.RippleTheme
import com.ivianuu.essentials.util.isDark

@Composable
fun guessingContentColorFor(color: Color): Color {
    return with(MaterialTheme.colors()) {
        when (color) {
            primary -> onPrimary
            primaryVariant -> onPrimary
            secondary -> onSecondary
            secondaryVariant -> onSecondary
            background -> onBackground
            surface -> onSurface
            error -> onError
            else -> if (color.isDark) Color.White else Color.Black
        }
    }
}

@Composable
fun RippleColorProvider(
    color: Color,
    children: @Composable () -> Unit
) {
    val theme = remember(color) {
        RippleTheme(
            factory = DefaultRippleEffectFactory,
            defaultColor = { color },
            opacity = { color.alpha }
        )
    }
    CurrentRippleTheme.Provider(value = theme, children = children)
}
