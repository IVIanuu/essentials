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

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.ui.animatedstack.*

class ScaleStackTransition(
    private val spec: AnimationSpec<Float> = defaultAnimationSpec()
) : StackTransition {
    override fun createSpec(isPush: Boolean): AnimationSpec<Float> = spec
    override fun createToModifier(progress: Float, isPush: Boolean): Modifier =
        if (isPush) Modifier.scale(scaleX = progress, scaleY = progress) else Modifier
    override fun createFromModifier(progress: Float, isPush: Boolean): Modifier =
        if (isPush) Modifier else Modifier.scale(scaleX = 1f - progress, scaleY = 1f - progress)
}
