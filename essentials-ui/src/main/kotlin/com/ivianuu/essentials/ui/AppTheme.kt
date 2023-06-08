/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivianuu.essentials.ui.animation.transition.FadeUpwardsStackTransition
import com.ivianuu.essentials.ui.animation.transition.LocalStackTransition
import com.ivianuu.essentials.ui.animation.transition.StackTransition
import com.ivianuu.essentials.ui.material.colors
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

data class AppColors(
  val primary: Color,
  val secondary: Color
) {
  companion object {
    @Provide val default: AppColors
      get() = AppColors(primary = Color(0xFF6200EE), secondary = Color(0xFF03DAC6))
  }
}

typealias AppFont = @AppFontTag FontFamily

@Tag annotation class AppFontTag

typealias AppTypography = @AppTypographyTag Typography

@Tag annotation class AppTypographyTag {
  companion object {
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
  companion object {
    @Provide val default: AppShapes
      get() = Shapes(
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp)
      )
  }
}

typealias AppTransition = @AppTransitionTag StackTransition

@Tag annotation class AppTransitionTag {
  companion object {
    @Provide val default: AppTransition
      get() = FadeUpwardsStackTransition()
  }
}

@Provide fun appThemeDecorator(
  colors: AppColors,
  shapes: AppShapes,
  typography: AppTypography,
  transition: AppTransition
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
      LocalStackTransition provides transition,
      content = content
    )
  }
}
