/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.util.*

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
          com.ivianuu.essentials.lerp(
            0.92f,
            1f,
            LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
          )
        )
    }
    from?.value = Modifier.alpha(com.ivianuu.essentials.lerp(1f, 0f, FastOutLinearInEasing.transform(value)))
  }
}
