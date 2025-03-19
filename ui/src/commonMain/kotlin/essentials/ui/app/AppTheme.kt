/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.app

import android.content.res.*
import androidx.compose.material.ripple.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.materialkolor.*
import essentials.*
import injekt.*

data class AppColors(
  val primary: Color,
  val secondary: Color? = null,
  val tertiary: Color? = null
) {
  @Provide companion object {
    @Provide val default: AppColors
      get() = AppColors(primary = Color(0xFF6200EE))
  }
}

@Tag typealias IsSystemInDarkTheme = Boolean
@Provide @Composable fun isSystemInDarkTheme(configuration: Configuration): IsSystemInDarkTheme =
  (configuration.uiMode  and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

@Tag typealias AppColorScheme = ColorScheme

@Provide @Composable fun defaultAppColorScheme(
  appColors: AppColors,
  isSystemInDarkTheme: IsSystemInDarkTheme
): AppColorScheme = remember(appColors, isSystemInDarkTheme) {
  dynamicColorScheme(
    isDark = isSystemInDarkTheme,
    isAmoled = false,
    primary = appColors.primary,
    secondary = appColors.secondary,
    tertiary = appColors.tertiary,
    style = PaletteStyle.FruitSalad
  )
}

@Tag typealias AppFont = FontFamily

@Tag typealias AppTypography = Typography

@Provide @Composable fun defaultAppTypography(font: AppFont? = null): AppTypography {
  val default = Typography()
  return Typography(
    titleLarge = default.titleLarge.copy(
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp
    ),

    bodyLarge = default.bodyLarge.copy(
      fontFamily = font,
      fontWeight = FontWeight.Medium,
      fontSize = 16.sp
    ),
    bodyMedium = default.bodyMedium.copy(
      fontFamily = font,
      fontWeight = FontWeight.Normal,
      fontSize = 14.sp
    )
  ).withFontFamily(font)
}

fun Typography.withFontFamily(fontFamily: FontFamily?): Typography = copy(
  displayLarge = displayLarge.copy(fontFamily = fontFamily),
  displayMedium = displayMedium.copy(fontFamily = fontFamily),
  displaySmall = displaySmall.copy(fontFamily = fontFamily),
  headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
  headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
  headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
  titleLarge = titleLarge.copy(fontFamily = fontFamily),
  titleMedium = titleMedium.copy(fontFamily = fontFamily),
  titleSmall = titleSmall.copy(fontFamily = fontFamily),
  bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
  bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
  bodySmall = bodySmall.copy(fontFamily = fontFamily),
  labelLarge = labelLarge.copy(fontFamily = fontFamily),
  labelMedium = labelMedium.copy(fontFamily = fontFamily),
  labelSmall = labelSmall.copy(fontFamily = fontFamily)
)

@Tag typealias AppShapes = Shapes

@Provide val defaultAppShapes: AppShapes
  get() = Shapes()

@Provide data object AppTheme {
  @Provide val loadingOrder
    get() = LoadingOrder<AppTheme>().first()
}

@Provide @Composable fun AppThemeDecorator(
  colorScheme: AppColorScheme,
  shapes: AppShapes,
  typography: AppTypography,
  content: @Composable () -> Unit
): AppUiDecoration<AppTheme> {
  MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = shapes) {
    CompositionLocalProvider(
      LocalRippleConfiguration provides RippleConfiguration(
        rippleAlpha = RippleAlpha(
          pressedAlpha = 0.24f * 1.5f,
          focusedAlpha = 0.24f * 1.5f,
          draggedAlpha = 0.16f * 1.5f,
          hoveredAlpha = 0.08f * 1.5f
        )
      ),
      content = content
    )
  }
}
