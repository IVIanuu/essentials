/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.app

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
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

fun interface ColorSchemeProducer {
  @Composable fun colorScheme(isDark: Boolean): ColorScheme

  @Provide companion object {
    @Provide fun default(appColors: AppColors) = ColorSchemeProducer {
      remember(it) {
        dynamicColorScheme(
          isDark = it,
          isAmoled = false,
          primary = appColors.primary,
          secondary = appColors.secondary,
          tertiary = appColors.tertiary,
          style = PaletteStyle.Vibrant
        )
      }
    }
  }
}

typealias AppFont = @AppFontTag FontFamily?

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class AppFontTag

typealias AppTypography = @AppTypographyTag Typography

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class AppTypographyTag {
  @Provide companion object {
    @Provide fun default(font: AppFont? = null): AppTypography = Typography()
      .withFontFamily(font)
  }
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

typealias AppShapes = @AppShapesTag Shapes

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class AppShapesTag {
  @Provide companion object {
    @Provide val default: AppShapes
      get() = Shapes()
  }
}

fun interface AppThemeDecorator : AppUiDecorator {
  @Provide companion object {
    @Provide val loadingOrder
      get() = LoadingOrder<AppThemeDecorator>().first()
  }
}

@Provide fun appThemeDecorator(
  colorSchemeProducer: ColorSchemeProducer,
  shapes: AppShapes,
  typography: AppTypography
) = AppThemeDecorator { content ->
  MaterialTheme(
    colorScheme = colorSchemeProducer.colorScheme(isSystemInDarkTheme()),
    typography = typography,
    shapes = shapes,
    content = content
  )
}
