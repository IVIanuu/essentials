/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.ivianuu.essentials.ui.animation.transition.FadeUpwardsStackTransition
import com.ivianuu.essentials.ui.animation.transition.LocalStackTransition
import com.ivianuu.essentials.ui.animation.transition.StackTransition
import com.ivianuu.essentials.ui.material.editEach
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
    @Provide fun default(font: AppFont? = null): AppTypography = Typography()
      .editEach { copy(fontFamily = font) }
  }
}

typealias AppShapes = @AppShapesTag Shapes

@Tag annotation class AppShapesTag {
  companion object {
    @Provide val default: AppShapes
      get() = Shapes()
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
    colorScheme = if (isSystemInDarkTheme()) darkColorScheme(
      primary = colors.primary,
      secondary = colors.secondary
    ) else lightColorScheme(
      primary = colors.primary,
      secondary = colors.secondary
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
