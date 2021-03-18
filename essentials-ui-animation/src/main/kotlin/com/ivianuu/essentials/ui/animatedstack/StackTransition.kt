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

package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import kotlin.time.Duration
import kotlin.time.milliseconds

interface StackTransition {
    fun createSpec(isPush: Boolean): AnimationSpec<Float>
    fun createToModifier(
        progress: Float,
        isPush: Boolean
    ): Modifier
    fun createFromModifier(
        progress: Float,
        isPush: Boolean
    ): Modifier
}

object NoOpStackTransition : StackTransition {
    override fun createSpec(isPush: Boolean): AnimationSpec<Float> = tween(durationMillis = 0)
    override fun createToModifier(progress: Float, isPush: Boolean): Modifier = Modifier
    override fun createFromModifier(progress: Float, isPush: Boolean): Modifier = Modifier
}

val LocalStackTransition =
    staticCompositionLocalOf<StackTransition> { NoOpStackTransition }

fun defaultAnimationSpec(
    duration: Duration = 300.milliseconds,
    delay: Duration = 0.milliseconds,
    easing: Easing = FastOutSlowInEasing
) = TweenSpec<Float>(
    durationMillis = duration.toLongMilliseconds().toInt(),
    delay = delay.toLongMilliseconds().toInt(),
    easing = easing
)
