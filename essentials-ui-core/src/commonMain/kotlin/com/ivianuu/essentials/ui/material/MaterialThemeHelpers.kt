/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle

fun Typography.editEach(edit: TextStyle.() -> TextStyle) = Typography(
  displayLarge = displayLarge.edit(),
  displayMedium = displayMedium.edit(),
  displaySmall = displaySmall.edit(),
  headlineLarge = headlineLarge.edit(),
  headlineMedium = headlineMedium.edit(),
  headlineSmall = headlineSmall.edit(),
  titleLarge = titleLarge.edit(),
  titleMedium = titleMedium.edit(),
  titleSmall = titleSmall.edit(),
  bodyLarge = bodyLarge.edit(),
  bodyMedium = bodyMedium.edit(),
  bodySmall = bodySmall.edit(),
  labelLarge = labelLarge.edit(),
  labelMedium = labelMedium.edit(),
  labelSmall = labelSmall.edit()
)

fun lerp(
  start: ColorScheme,
  end: ColorScheme,
  fraction: Float
) = ColorScheme(
  primary = lerp(start.primary, end.primary, fraction),
  onPrimary = lerp(start.onPrimary, end.onPrimary, fraction),
  primaryContainer = lerp(start.primaryContainer, end.primaryContainer, fraction),
  onPrimaryContainer = lerp(start.onPrimaryContainer, end.onPrimaryContainer, fraction),
  inversePrimary = lerp(start.inversePrimary, end.inversePrimary, fraction),
  secondary = lerp(start.secondary, end.secondary, fraction),
  onSecondary = lerp(start.onSecondary, end.onSecondary, fraction),
  secondaryContainer = lerp(start.secondaryContainer, end.secondaryContainer, fraction),
  onSecondaryContainer = lerp(start.onSecondaryContainer, end.onSecondaryContainer, fraction),
  tertiary = lerp(start.tertiary, end.tertiary, fraction),
  onTertiary = lerp(start.onTertiary, end.onTertiary, fraction),
  tertiaryContainer = lerp(start.tertiaryContainer, end.tertiaryContainer, fraction),
  onTertiaryContainer = lerp(start.onTertiaryContainer, end.onTertiaryContainer, fraction),
  background = lerp(start.background, end.background, fraction),
  onBackground = lerp(start.onBackground, end.onBackground, fraction),
  surface = lerp(start.surface, end.surface, fraction),
  onSurface = lerp(start.onSurface, end.onSurface, fraction),
  surfaceVariant = lerp(start.surfaceVariant, end.surfaceVariant, fraction),
  onSurfaceVariant = lerp(start.onSurfaceVariant, end.onSurfaceVariant, fraction),
  surfaceTint = lerp(start.surfaceTint, end.surfaceTint, fraction),
  inverseSurface = lerp(start.inverseSurface, end.inverseSurface, fraction),
  inverseOnSurface = lerp(start.inverseOnSurface, end.inverseOnSurface, fraction),
  error = lerp(start.error, end.error, fraction),
  onError = lerp(start.onError, end.onError, fraction),
  errorContainer = lerp(start.errorContainer, end.errorContainer, fraction),
  onErrorContainer = lerp(start.onErrorContainer, end.onErrorContainer, fraction),
  outline = lerp(start.outline, end.outline, fraction),
  outlineVariant = lerp(start.outlineVariant, end.outlineVariant, fraction),
  scrim = lerp(start.scrim, end.scrim, fraction)
)
