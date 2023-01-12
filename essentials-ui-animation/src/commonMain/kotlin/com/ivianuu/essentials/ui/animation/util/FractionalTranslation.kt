/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.util

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.setValue

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
