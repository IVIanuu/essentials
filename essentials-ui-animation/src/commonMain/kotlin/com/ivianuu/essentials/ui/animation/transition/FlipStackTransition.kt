/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

fun FlipStackTransition(
  direction: FlipDirection = FlipDirection.RIGHT,
  spec: AnimationSpec<Float> = defaultAnimationSpec(easing = FastOutSlowInEasing)
) = ContentAnimationStackTransition(spec) { fromModifier, toModifier, value ->
  toModifier?.value = Modifier
    .graphicsLayer {
      alpha = value
      if (direction == FlipDirection.LEFT || direction == FlipDirection.RIGHT)
        rotationY = direction.startRotation - direction.startRotation * value
      else
        rotationX = direction.startRotation - direction.startRotation * value
    }
  fromModifier?.value = Modifier
    .graphicsLayer {
      alpha = 1f - value
      if (direction == FlipDirection.LEFT || direction == FlipDirection.RIGHT)
        rotationY = direction.endRotation * value
      else
        rotationX = direction.endRotation * value
    }
}

enum class FlipDirection(
  val startRotation: Float,
  val endRotation: Float
) {
  LEFT(-180f, 180f),
  RIGHT(180f, -180f),
  UP(-180f, 180f),
  DOWN(180f, -180f);
}
