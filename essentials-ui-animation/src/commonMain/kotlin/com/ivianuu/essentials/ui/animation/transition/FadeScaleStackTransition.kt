/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import com.ivianuu.essentials.ui.animation.util.interval

object ScrimAnimationElementKey

object PopupAnimationElementKey

fun FadeScaleStackTransition(
  origin: TransformOrigin = TransformOrigin.Center,
  enterSpec: AnimationSpec<Float> = defaultAnimationSpec(150.milliseconds),
  exitSpec: AnimationSpec<Float> = defaultAnimationSpec(75.milliseconds)
): StackTransition = {
  if (isPush) {
    val scrim = toElementModifier(ScrimAnimationElementKey)
    val popup = toElementModifier(PopupAnimationElementKey)
    val from = if (fromWillBeRemoved) fromElementModifier(ContentAnimationElementKey) else null
    scrim?.value = Modifier.alpha(0f)
    popup?.value = Modifier.alpha(0f)
    attachTo()
    animate(enterSpec) { value ->
      from?.value = Modifier.alpha(1f - value)
      scrim?.value = Modifier.alpha(value)
      popup?.value = Modifier
        .graphicsLayer {
          transformOrigin = origin
          alpha = interval(0f, 0.3f, value)
          scaleX = lerp(0.8f, 1f, LinearOutSlowInEasing.transform(value))
          scaleY = lerp(0.8f, 1f, LinearOutSlowInEasing.transform(value))
        }
    }
  } else {
    val scrim = fromElementModifier(ScrimAnimationElementKey)
    val popup = fromElementModifier(PopupAnimationElementKey)
    animate(exitSpec) { value ->
      scrim?.value = Modifier.alpha(1f - value)
      popup?.value = Modifier.alpha(1f - value)
    }
  }
}
