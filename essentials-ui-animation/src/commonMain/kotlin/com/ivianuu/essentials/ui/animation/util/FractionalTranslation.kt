/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.util

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*

fun Modifier.fractionalTranslation(
  xFraction: Float = 0f,
  yFraction: Float = 0f
) = composed {
  var size by remember { mutableStateOf(IntSize(Int.MAX_VALUE, Int.MAX_VALUE)) }
  onSizeChanged { size = it }
    .graphicsLayer {
      translationX = size.width * xFraction
      translationY = size.height * yFraction
    }
}
