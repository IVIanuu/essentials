/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.util.*
import com.ivianuu.injekt.*

data class AppColors(
  val primary: Color,
  val secondary: Color
) {
  @Provide companion object {
    @Provide val default: AppColors
      get() = AppColors(primary = Color(0xFF6200EE), secondary = Color(0xFF03DAC6))
  }
}

typealias AppFont = @AppFontTag FontFamily

@Tag annotation class AppFontTag

typealias AppTypography = @AppTypographyTag Typography

@Tag annotation class AppTypographyTag {
  @Provide companion object {
    @Provide fun default(font: AppFont? = null): AppTypography = Typography(
      h1 = TextStyle(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.2).sp,
        fontFamily = font
      ),
      h2 = TextStyle(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp,
        fontFamily = font
      ),
      h3 = TextStyle(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp,
        fontFamily = font
      ),
      h5 = TextStyle(
        fontSize = 28.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp,
        fontFamily = font
      ),
      h6 = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp,
        fontFamily = font
      ),
      subtitle1 = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
        fontFamily = font
      ),
      subtitle2 = TextStyle(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
        fontFamily = font
      ),
      body2 = TextStyle(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.sp,
        fontFamily = font
      ),
      button = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
        fontFamily = font
      ),
      caption = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        fontFamily = font
      ),
      overline = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        fontFamily = font
      )
    )
  }
}

typealias AppShapes = @AppShapesTag Shapes

@Tag annotation class AppShapesTag {
  @Provide companion object {
    @Provide val default: AppShapes
      get() = Shapes(
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp)
      )
  }
}

typealias AppScreenTransitionSpec = @AppTransitionScreenSpecTag ElementTransitionSpec<Screen<*>>

@Tag annotation class AppTransitionScreenSpecTag {
  @Provide companion object {
    @Provide val default: AppScreenTransitionSpec
      get() = {
        fadeUpwards()
      }
  }
}

@Provide fun appThemeDecorator(
  colors: AppColors,
  shapes: AppShapes,
  typography: AppTypography,
  transitionSpec: AppScreenTransitionSpec
) = AppThemeDecorator { content ->
  MaterialTheme(
    colors = if (isSystemInDarkTheme()) colors(
      isLight = false,
      primary = colors.primary,
      primaryVariant = colors.primary,
      secondary = colors.secondary,
      secondaryVariant = colors.secondary
    ) else colors(
      isLight = true,
      primary = colors.primary,
      primaryVariant = colors.primary,
      secondary = colors.secondary,
      secondaryVariant = colors.secondary
    ),
    typography = typography,
    shapes = shapes
  ) {
    CompositionLocalProvider(
      LocalScreenTransitionSpec provides transitionSpec,
      content = content
    )
  }
}
