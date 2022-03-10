/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.util.*

fun HorizontalSharedAxisStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = {
  val from = fromElementModifier(ContentAnimationElementKey)
  val to = toElementModifier(ContentAnimationElementKey)
  to?.value = Modifier.alpha(0f)
  attachTo()
  animate(spec) { value ->
    if (isPush) {
      to?.value = Modifier.graphicsLayer {
        alpha = LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
        translationX = com.ivianuu.essentials.lerp(30.dp.toPx(), 0f, FastOutSlowInEasing.transform(value))
      }
      from?.value = Modifier.graphicsLayer {
        alpha = com.ivianuu.essentials.lerp(1f, 0f, LinearOutSlowInEasing.transform(interval(0f, 0.3f, value)))
        translationX = com.ivianuu.essentials.lerp(0f, -30.dp.toPx(), FastOutSlowInEasing.transform(value))
      }
    } else {
      to?.value = Modifier.graphicsLayer {
        alpha = LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
        translationX = com.ivianuu.essentials.lerp(-30.dp.toPx(), 0f, FastOutSlowInEasing.transform(value))
      }
      from?.value = Modifier.graphicsLayer {
        alpha = com.ivianuu.essentials.lerp(1f, 0f, LinearOutSlowInEasing.transform(interval(0f, 0.3f, value)))
        translationX = com.ivianuu.essentials.lerp(0f, 30.dp.toPx(), FastOutSlowInEasing.transform(value))
      }
    }
  }
}

fun VerticalSharedAxisStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = {
  val from = fromElementModifier(ContentAnimationElementKey)
  val to = toElementModifier(ContentAnimationElementKey)
  to?.value = Modifier.alpha(0f)
  attachTo()
  animate(spec) { value ->
    if (isPush) {
      to?.value = Modifier.graphicsLayer {
        alpha = LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
        translationY = com.ivianuu.essentials.lerp(30.dp.toPx(), 0f, FastOutSlowInEasing.transform(value))
      }
      from?.value = Modifier.graphicsLayer {
        alpha = com.ivianuu.essentials.lerp(1f, 0f, LinearOutSlowInEasing.transform(interval(0f, 0.3f, value)))
        translationY = com.ivianuu.essentials.lerp(0f, -30.dp.toPx(), FastOutSlowInEasing.transform(value))
      }
    } else {
      to?.value = Modifier.graphicsLayer {
        alpha = LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
        translationY = com.ivianuu.essentials.lerp(-30.dp.toPx(), 0f, FastOutSlowInEasing.transform(value))
      }
      from?.value = Modifier.graphicsLayer {
        alpha = com.ivianuu.essentials.lerp(1f, 0f, LinearOutSlowInEasing.transform(interval(0f, 0.3f, value)))
        translationY = com.ivianuu.essentials.lerp(0f, 30.dp.toPx(), FastOutSlowInEasing.transform(value))
      }
    }
  }
}

fun ScaledSharedAxisStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = {
  val from = fromElementModifier(ContentAnimationElementKey)
  val to = toElementModifier(ContentAnimationElementKey)
  to?.value = Modifier.alpha(0f)
  attachTo()
  animate(spec) { value ->
    if (isPush) {
      to?.value = Modifier
        .alpha(LinearOutSlowInEasing.transform(interval(0.1f, 0.5f, value)))
        .scale(com.ivianuu.essentials.lerp(0.85f, 1f, FastOutSlowInEasing.transform(value)))
      from?.value = Modifier
        .alpha(com.ivianuu.essentials.lerp(1f, 0.4f, LinearOutSlowInEasing.transform(interval(0f, 0.5f, value))))
        .scale(com.ivianuu.essentials.lerp(1f, 1.05f, FastOutSlowInEasing.transform(value)))
    } else {
      to?.value = Modifier
        .scale(com.ivianuu.essentials.lerp(1.05f, 1f, FastOutSlowInEasing.transform(value)))
      from?.value = Modifier
        .alpha(com.ivianuu.essentials.lerp(1f, 0f, LinearOutSlowInEasing.transform(interval(0f, 0.5f, value))))
        .scale(com.ivianuu.essentials.lerp(1f, 0.85f, FastOutSlowInEasing.transform(value)))
    }
  }
}
