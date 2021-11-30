/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import com.ivianuu.essentials.ui.animation.util.interval

fun FadeThroughStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = {
  val from = fromElementModifier(ContentAnimationElementKey)
  val to = toElementModifier(ContentAnimationElementKey)
  to?.value = Modifier.alpha(0f)
  attachTo()
  animate(spec) { value ->
    if (isPush) {
      to?.value = Modifier
        .alpha(LinearOutSlowInEasing.transform(interval(0.3f, 1f, value)))
        .scale(
          lerp(
            0.92f,
            1f,
            LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
          )
        )
    }
    from?.value = Modifier.alpha(lerp(1f, 0f, FastOutLinearInEasing.transform(value)))
  }
}
