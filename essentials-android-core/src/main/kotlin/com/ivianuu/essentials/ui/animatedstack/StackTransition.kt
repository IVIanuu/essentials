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
import androidx.ui.unit.PxBounds
import com.ivianuu.essentials.ui.animatable.AnimatableElement

data class StackTransitionContext(
    val fromElement: AnimatableElement?,
    val toElement: AnimatableElement?,
    val containerBounds: PxBounds?,
    val isPush: Boolean,
    private val addToBlock: () -> Unit,
    private val removeFromBlock: () -> Unit,
    private val onCompleteBlock: () -> Unit
) {
    fun addTo() {
        addToBlock()
    }

    fun removeFrom() {
        removeFromBlock()
    }

    fun onComplete() {
        onCompleteBlock()
    }
}

typealias StackTransition = @Composable (StackTransitionContext) -> Unit

val NoOpStackTransition: StackTransition = { context ->
    if (context.toElement != null) context.addTo()
    if (context.fromElement != null) context.removeFrom()
    onActive { context.onComplete() }
}

val DefaultStackAnimationAmbient =
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
        context.copy(
            onCompleteBlock = {
                completedAnimations += thisAnimation
                completeIfPossible()
            }
        )
    )

    other(
        context.copy(
            onCompleteBlock = {
                completedAnimations += other
                completeIfPossible()
            }
        )
    )

}
