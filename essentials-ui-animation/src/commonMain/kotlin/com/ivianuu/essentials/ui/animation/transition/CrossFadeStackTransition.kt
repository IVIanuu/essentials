/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

fun CrossFadeStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec()
) = ContentAnimationStackTransition(spec) { fromModifier, toModifier, value ->
  fromModifier?.value = Modifier.alpha(1f - value)
  toModifier?.value = Modifier.alpha(value)
}
