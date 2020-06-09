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
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier

typealias StackAnimation = @Composable (
    hasFrom: Boolean,
    hasTo: Boolean,
    isPush: Boolean,
    onComplete: () -> Unit
) -> StackAnimationModifiers

val NoOpStackAnimation: StackAnimation = { _, _, _, onComplete ->
    onCommit { onComplete() }
    NoOpStackAnimationModifiers
}

private val NoOpStackAnimationModifiers = StackAnimationModifiers(
    to = { Modifier },
    from = { Modifier }
)

data class StackAnimationModifiers(
    val to: @Composable (() -> Modifier)?,
    val from: @Composable (() -> Modifier)?
)

val DefaultStackAnimationAmbient =
    staticAmbientOf { NoOpStackAnimation }

operator fun StackAnimation.plus(other: StackAnimation): StackAnimation =
    { hasFrom, hasTo, isPush, onComplete ->
        val thisAnimation = this

        val completedAnimations = remember { mutableSetOf<StackAnimation>() }

        fun completeIfPossible() {
            if (thisAnimation in completedAnimations && other in completedAnimations) {
                onComplete()
            }
        }

        val thisModifiers = thisAnimation(hasFrom, hasTo, isPush) {
            completedAnimations += thisAnimation
            completeIfPossible()
        }

        val otherModifiers = other(hasFrom, hasTo, isPush) {
            completedAnimations += other
            completeIfPossible()
        }

        StackAnimationModifiers(
            to = if (thisModifiers.to != null || otherModifiers.to != null) ({
                (thisModifiers.to?.invoke() ?: Modifier) + (otherModifiers.to?.invoke() ?: Modifier)
            }) else null,
            from = if (thisModifiers.from != null || otherModifiers.from != null) ({
                (thisModifiers.from?.invoke() ?: Modifier) + (otherModifiers.from?.invoke()
                    ?: Modifier)
            }) else null,
        )
}
