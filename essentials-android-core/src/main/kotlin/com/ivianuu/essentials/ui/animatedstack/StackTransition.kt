/*
 * Copyright 2019 Manuel Wrage
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

import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.animatable.Animatable

abstract class StackTransitionContext(
    val fromAnimatable: Animatable?,
    val toAnimatable: Animatable?,
    val isPush: Boolean
) {
    abstract fun addTo()
    abstract fun removeFrom()
    abstract fun onComplete()
}

typealias StackTransition = @Composable (StackTransitionContext) -> Unit

val NoOpStackTransition: StackTransition = { context ->
    if (context.toAnimatable != null) context.addTo()
    if (context.fromAnimatable != null) context.removeFrom()
    onActive { context.onComplete() }
}

val DefaultStackTransitionAmbient =
    staticAmbientOf { NoOpStackTransition }

operator fun StackTransition.plus(other: StackTransition): StackTransition = { context ->
    val thisAnimation = this@plus

    val completedAnimations = remember { mutableSetOf<StackTransition>() }

    fun completeIfPossible() {
        if (thisAnimation in completedAnimations && other in completedAnimations) {
            context.onComplete()
        }
    }

    thisAnimation(
        remember {
            object : StackTransitionContext(
                context.fromAnimatable,
                context.toAnimatable,
                context.isPush
            ) {
                override fun addTo() {
                    context.addTo()
                }

                override fun removeFrom() {
                    context.removeFrom()
                }

                override fun onComplete() {
                    completedAnimations += thisAnimation
                    completeIfPossible()
                }
            }
        }
    )

    other(
        remember {
            object : StackTransitionContext(
                context.fromAnimatable,
                context.toAnimatable,
                context.isPush
            ) {
                override fun addTo() {
                    context.addTo()
                }

                override fun removeFrom() {
                    context.removeFrom()
                }

                override fun onComplete() {
                    completedAnimations += other
                    completeIfPossible()
                }
            }
        }
    )

}
