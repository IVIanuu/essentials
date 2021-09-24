package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivianuu.essentials.ui.animation.transition.LocalStackTransition
import com.ivianuu.essentials.ui.animation.transition.ScaledSharedAxisStackTransition
import com.ivianuu.essentials.ui.animation.transition.StackTransition

@Composable fun EsTheme(
  lightColors: Colors = lightColors(),
  darkColors: Colors = darkColors(),
  typography: Typography = EsTypography,
  shapes: Shapes = EsShapes,
  transition: StackTransition = ScaledSharedAxisStackTransition(),
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

val EsTypography = Typography(
  subtitle1 = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    letterSpacing = 0.15.sp
  )
)

val EsShapes = Shapes(
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(20.dp)
)
