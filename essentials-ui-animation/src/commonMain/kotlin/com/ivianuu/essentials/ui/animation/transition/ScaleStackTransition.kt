/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*

fun ScaleStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec(easing = FastOutSlowInEasing)
) = ContentAnimationStackTransition(spec) { fromModifier, toModifier, value ->
  fromModifier?.value = if (isPush) Modifier
  else Modifier.scale(scaleX = 1f - value, scaleY = 1f - value)
  toModifier?.value = if (isPush) Modifier.scale(scaleX = value, scaleY = value)
  else Modifier
}
