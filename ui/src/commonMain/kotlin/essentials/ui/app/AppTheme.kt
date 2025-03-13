/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.app

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
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

@Tag typealias AppColorScheme = ColorScheme
@Tag typealias IsDark = Boolean

@Provide @Composable fun defaultAppColorScheme(
  appColors: AppColors,
  isDark: IsDark
): AppColorScheme = remember(isDark) {
  dynamicColorScheme(
    isDark = isDark,
    isAmoled = false,
    primary = appColors.primary,
    secondary = appColors.secondary,
    tertiary = appColors.tertiary,
    style = PaletteStyle.Vibrant
  )
}

@Tag typealias AppFont = FontFamily

@Tag typealias AppTypography = Typography

@Provide fun defaultAppTypography(font: AppFont? = null): AppTypography =
  Typography().withFontFamily(font)

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

fun interface AppThemeDecorator : AppUiDecorator {
  @Provide companion object {
    @Provide val loadingOrder
      get() = LoadingOrder<AppThemeDecorator>().first()
  }
}

@Provide fun appThemeDecorator(
  colorSchemeProducer: @Composable (IsDark) -> AppColorScheme,
  shapes: AppShapes,
  typography: AppTypography
) = AppThemeDecorator { content ->
  MaterialTheme(
    colorScheme = colorSchemeProducer(isSystemInDarkTheme()),
    typography = typography,
    shapes = shapes,
    content = content
  )
}
