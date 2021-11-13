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
