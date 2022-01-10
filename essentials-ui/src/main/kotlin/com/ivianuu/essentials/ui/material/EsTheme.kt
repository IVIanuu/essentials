/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.transition.*

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
  h1 = TextStyle(
    fontSize = 57.sp,
    lineHeight = 64.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.2).sp
  ),
  h2 = TextStyle(
    fontSize = 45.sp,
    lineHeight = 52.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.0.sp
  ),
  h3 = TextStyle(
    fontSize = 45.sp,
    lineHeight = 52.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.0.sp
  ),
  h5 = TextStyle(
    fontSize = 28.sp,
    lineHeight = 32.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.0.sp
  ),
  h6 = TextStyle(
    fontSize = 22.sp,
    lineHeight = 28.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.0.sp
  ),
  subtitle1 = TextStyle(
    fontSize = 18.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.2.sp
  ),
  subtitle2 = TextStyle(
    fontSize = 16.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.1.sp
  ),
  body2 = TextStyle(
    fontSize = 16.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.2.sp
  ),
  button = TextStyle(
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.1.sp
  ),
  caption = TextStyle(
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.5.sp
  ),
  overline = TextStyle(
    fontSize = 11.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.5.sp
  )
)

val EsShapes = Shapes(
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(20.dp)
)
