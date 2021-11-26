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

package com.ivianuu.essentials.ui.material

import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import com.ivianuu.essentials.ui.util.isDark

fun Typography.editEach(edit: TextStyle.() -> TextStyle) = Typography(
  h1 = edit(h1),
  h2 = edit(h2),
  h3 = edit(h3),
  h4 = edit(h4),
  h5 = edit(h5),
  h6 = edit(h6),
  subtitle1 = edit(subtitle1),
  subtitle2 = edit(subtitle2),
  body1 = edit(body1),
  body2 = edit(body2),
  button = edit(button),
  caption = edit(caption),
  overline = edit(overline)
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

fun Colors.copy(
  isLight: Boolean = this.isLight,
  primary: Color = this.primary,
  primaryVariant: Color = this.primaryVariant,
  secondary: Color = this.secondary,
  secondaryVariant: Color = this.secondaryVariant,
  background: Color = this.background,
  surface: Color = this.surface,
  error: Color = this.error,
  onPrimary: Color = this.onPrimary,
  onSecondary: Color = this.onSecondary,
  onBackground: Color = this.onBackground,
  onSurface: Color = this.onSurface,
  onError: Color = this.onError
) = Colors(
  isLight = isLight,
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

fun lerp(
  start: Colors,
  end: Colors,
  fraction: Float
) = Colors(
  isLight = if (fraction < 0.5) start.isLight else end.isLight,
  primary = lerp(start.primary, end.primary, fraction),
  primaryVariant = lerp(start.primaryVariant, end.primaryVariant, fraction),
  secondary = lerp(start.secondary, end.secondary, fraction),
  secondaryVariant = lerp(start.secondaryVariant, end.secondaryVariant, fraction),
  background = lerp(start.background, end.background, fraction),
  surface = lerp(start.surface, end.surface, fraction),
  error = lerp(start.error, end.error, fraction),
  onPrimary = lerp(start.onPrimary, end.onPrimary, fraction),
  onSecondary = lerp(start.onSecondary, end.onSecondary, fraction),
  onBackground = lerp(start.onBackground, end.onBackground, fraction),
  onSurface = lerp(start.onSurface, end.onSurface, fraction),
  onError = lerp(start.onError, end.onError, fraction)
)
