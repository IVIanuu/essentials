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
