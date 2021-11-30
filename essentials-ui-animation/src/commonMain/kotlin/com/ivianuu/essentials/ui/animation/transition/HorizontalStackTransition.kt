/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.animation.util.fractionalTranslation

fun HorizontalStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec(easing = FastOutSlowInEasing)
) = ContentAnimationStackTransition(spec) { fromModifier, toModifier, value ->
  fromModifier?.value = Modifier
    .fractionalTranslation(xFraction = if (isPush) -value else value)
  toModifier?.value = Modifier
    .fractionalTranslation(xFraction = if (isPush) (1f - value) else -1f + value)
}