/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import com.ivianuu.essentials.ui.animation.util.fractionalTranslation

fun FadeUpwardsStackTransition(spec: AnimationSpec<Float> = defaultAnimationSpec()): StackTransition =
  {
    val target = if (isPush) toElementModifier(ContentAnimationElementKey)
    else fromElementModifier(ContentAnimationElementKey)
    if (isPush) target?.value = Modifier.alpha(0f)
    attachTo()
    animate(spec) { value ->
      target?.value = Modifier
        .fractionalTranslation(
          yFraction = lerp(
            if (isPush) 0.25f else 0f,
            if (isPush) 0f else 0.25f,
            FastOutSlowInEasing.transform(value)
          )
        )
        .alpha(
          lerp(
            if (isPush) 0f else 1f,
            if (isPush) 1f else 0f,
            FastOutLinearInEasing.transform(value)
          )
        )
    }
  }
