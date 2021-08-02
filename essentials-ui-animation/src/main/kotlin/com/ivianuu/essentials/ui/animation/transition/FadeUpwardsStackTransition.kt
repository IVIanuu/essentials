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

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.util.*

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
