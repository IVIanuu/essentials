/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.common

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*

fun Modifier.interactive(
  interactive: Boolean,
  animateAlpha: Boolean = true
): Modifier = composed {
  val targetAlpha = if (interactive) 1f else 0.5f
  alpha(
    alpha = if (animateAlpha) animateFloatAsState(targetAlpha).value
    else targetAlpha
  )
    .consumeGestures(!interactive)
}
