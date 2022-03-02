/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.animation.util.*

fun VerticalStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec(easing = FastOutSlowInEasing)
) = ContentAnimationStackTransition(spec) { fromModifier, toModifier, value ->
  fromModifier?.value = if (isPush) Modifier else Modifier
    .fractionalTranslation(yFraction = value)
  toModifier?.value = if (isPush)
    Modifier.fractionalTranslation(yFraction = 1f - value)
  else Modifier
}
