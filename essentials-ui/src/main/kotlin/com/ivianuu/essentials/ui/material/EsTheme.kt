package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.ivianuu.essentials.ui.animation.transition.LocalStackTransition
import com.ivianuu.essentials.ui.animation.transition.StackTransition

@Composable fun EsTheme(
  lightColors: Colors = lightColors(),
  darkColors: Colors = darkColors(),
  typography: Typography = MaterialTheme.typography,
  shapes: Shapes = MaterialTheme.shapes,
  transition: StackTransition = LocalStackTransition.current,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colors = if (isSystemInDarkTheme()) darkColors else lightColors,
    typography = typography,
    shapes = shapes
  ) {
    CompositionLocalProvider(
      LocalStackTransition provides transition,
      content = content
    )
  }
}
