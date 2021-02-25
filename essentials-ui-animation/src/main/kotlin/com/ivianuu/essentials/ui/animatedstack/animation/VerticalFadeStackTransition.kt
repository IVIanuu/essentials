/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.animation.core.AnimationSpec
import com.ivianuu.essentials.ui.animatable.Alpha
import com.ivianuu.essentials.ui.animatable.setFractionTranslationY
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun VerticalFadeStackTransition(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
): StackTransition {
    return FloatAnimationStackTransition(animationSpec = animationSpec) { from, to, isPush, progress ->
        if (to != null && isPush) {
            to
                .set(Alpha, progress)
                .setFractionTranslationY(0.3f * (1f - progress))
        }
        if (from != null && !isPush) {
            from
                .set(Alpha, 1f - progress)
                .setFractionTranslationY(0.3f * progress)
        }
    }
}
