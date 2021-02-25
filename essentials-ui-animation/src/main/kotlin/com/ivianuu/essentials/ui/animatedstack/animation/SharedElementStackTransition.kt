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
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.ivianuu.essentials.ui.animatable.Alpha
import com.ivianuu.essentials.ui.animatable.MetaProp
import com.ivianuu.essentials.ui.animatable.animatable
import com.ivianuu.essentials.ui.animatable.animatableFor
import com.ivianuu.essentials.ui.animatable.animationOverlay
import com.ivianuu.essentials.ui.animatable.to
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.StackTransitionContext
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.util.lerp
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

val SharedElementComposable = MetaProp<@Composable () -> Unit>()

fun SharedElementStackTransition(
    vararg sharedElements: Pair<Any, Any>,
    sharedElementAnimationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    contentTransition: StackTransition = FadeStackTransition(sharedElementAnimationSpec),
    waitingTimeout: Duration = 200.milliseconds
): StackTransition = { context ->
    remember { context.addTo() }

    val animatables = sharedElements
        .mapIndexed { index, (from, to) ->
            key(index) {
                if (context.isPush) animatableFor(from) to animatableFor(to)
                else animatableFor(to) to animatableFor(from)
            }
        }

    val bounds = animatables
        .mapIndexed { index, (start, end) ->
            key(index) {
                start.rememberFirstNonNullBounds() to end.rememberFirstNonNullBounds()
            }
        }

    val animationState: AnimationState<Float, AnimationVector1D> = remember {
        AnimationState(0f)
    }

    val forceRun by produceState(false) {
        delay(waitingTimeout.toLongMilliseconds())
        value = true
    }

    if (bounds.all { it.first != null && it.second != null } || forceRun) {
        var otherComplete by remember { refOf(false) }
        var sharedElementComplete by remember { refOf(false) }

        fun completeIfPossible() {
            if (otherComplete && (sharedElementComplete || forceRun)) {
                context.removeFrom()
                context.onComplete()
            }
        }

        LaunchedEffect(true) {
            context.toAnimatable?.set(Alpha, 1f)

            animatables.forEach { (start, end) ->
                // hide the "real" elements
                start[Alpha] = 0f
                end[Alpha] = 0f
            }

            animationState.animateTo(
                targetValue = 1f,
                animationSpec = sharedElementAnimationSpec
            )

            sharedElementComplete = true
            animatables.forEach { (start, end) ->
                // show the "real" elements
                start[Alpha] = 1f
                end[Alpha] = 1f
            }
            completeIfPossible()
        }

        contentTransition(
            remember {
                object : StackTransitionContext(
                    context.fromAnimatable,
                    context.toAnimatable,
                    context.isPush
                ) {
                    override fun addTo() {
                    }

                    override fun removeFrom() {
                    }

                    override fun onComplete() {
                        otherComplete = true
                        completeIfPossible()
                    }
                }
            }
        )
    } else {
        context.toAnimatable?.set(Alpha, 0f)
    }

    val propPairs = bounds
        .filter { it.first != null && it.second != null }
        .map { it.first!! to it.second!! }
        .map { (capturedStartBounds, capturedEndBounds) ->
            val start = SharedElementProps(
                position = Offset(
                    x = capturedStartBounds.left + (capturedStartBounds.width - capturedEndBounds.width) / 2,
                    y = capturedStartBounds.top + (capturedStartBounds.height - capturedEndBounds.height) / 2
                ),
                scaleX = capturedStartBounds.width / capturedEndBounds.width,
                scaleY = capturedStartBounds.height / capturedEndBounds.height
            )
            val end = SharedElementProps(
                position = Offset(capturedEndBounds.left, capturedEndBounds.top),
                scaleX = 1f,
                scaleY = 1f
            )
            start to end
        }

    val animatedProps = propPairs
        .map { (startProps, endProps) ->
            SharedElementProps(
                position = lerp(startProps.position, endProps.position, animationState.value),
                scaleX = lerp(startProps.scaleX, endProps.scaleX, animationState.value),
                scaleY = lerp(startProps.scaleY, endProps.scaleY, animationState.value)
            )
        }

    if (animatedProps.isNotEmpty()) {
        animationOverlay {
            animatables.forEachIndexed { index, (_, endAnimatable) ->
                val (_, endBounds) = bounds[index]
                val currentProps = animatedProps[index]
                val sharedElementComposable =
                    endAnimatable.getOrElse(SharedElementComposable) { {} }
                with(LocalDensity.current) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = endBounds!!.width.toDp(),
                                height = endBounds.height.toDp()
                            )
                            .offset(
                                x = currentProps.position.x.toDp(),
                                y = currentProps.position.y.toDp()
                            )
                            .graphicsLayer(scaleX = currentProps.scaleX, scaleY = currentProps.scaleY)
                    ) {
                        sharedElementComposable()
                    }
                }
            }
        }
    }
}

data class SharedElementProps(
    val position: Offset,
    val scaleX: Float,
    val scaleY: Float
)

@Composable
fun SharedElement(
    tag: Any,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .animatable(tag, SharedElementComposable to content).then(modifier)
    ) {
        content()
    }
}
