package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.compose.ui.util.lerp
import com.ivianuu.essentials.ui.animatable.Alpha
import com.ivianuu.essentials.ui.animatable.MetaProp
import com.ivianuu.essentials.ui.animatable.animatable
import com.ivianuu.essentials.ui.animatable.animatableFor
import com.ivianuu.essentials.ui.animatable.animationOverlay
import com.ivianuu.essentials.ui.animatable.withValue
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.StackTransitionContext
import com.ivianuu.essentials.ui.common.untrackedState
import com.ivianuu.essentials.ui.coroutines.produceState
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

val SharedElementComposable = MetaProp<@Composable () -> Unit>()

fun SharedElementStackTransition(
    vararg sharedElements: Pair<Any, Any>,
    sharedElementAnim: AnimationSpec<Float> = defaultAnimationSpec(),
    contentTransition: StackTransition = FadeStackTransition(sharedElementAnim),
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

    val animation = animatedFloat(0f)

    val forceRun by produceState(false) {
        delay(waitingTimeout.toLongMilliseconds())
        true
    }

    if (bounds.all { it.first != null && it.second != null } || forceRun) {
        var otherComplete by untrackedState { false }
        var sharedElementComplete by untrackedState { false }

        fun completeIfPossible() {
            if (otherComplete && (sharedElementComplete || forceRun)) {
                context.removeFrom()
                context.onComplete()
            }
        }

        remember {
            context.toAnimatable?.set(Alpha, 1f)

            animatables.forEach { (start, end) ->
                // hide the "real" elements
                start[Alpha] = 0f
                end[Alpha] = 0f
            }

            animation.animateTo(
                targetValue = 1f,
                anim = sharedElementAnim,
                onEnd = { _, _ ->
                    sharedElementComplete = true
                    animatables.forEach { (start, end) ->
                        // show the "real" elements
                        start[Alpha] = 1f
                        end[Alpha] = 1f
                    }
                    completeIfPossible()
                }
            )
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
                position = lerp(startProps.position, endProps.position, animation.value),
                scaleX = lerp(startProps.scaleX, endProps.scaleX, animation.value),
                scaleY = lerp(startProps.scaleY, endProps.scaleY, animation.value)
            )
        }

    if (animatedProps.isNotEmpty()) {
        animationOverlay {
            animatables.forEachIndexed { index, (_, endAnimatable) ->
                val (_, endBounds) = bounds[index]
                val currentProps = animatedProps[index]
                val sharedElementComposable =
                    endAnimatable.getOrElse(SharedElementComposable) { {} }
                with(DensityAmbient.current) {
                    Box(
                        modifier = Modifier
                            .preferredSize(
                                width = endBounds!!.width.toDp(),
                                height = endBounds.height.toDp()
                            )
                            .offset(
                                x = currentProps.position.x.toDp(),
                                y = currentProps.position.y.toDp()
                            )
                            .drawLayer(scaleX = currentProps.scaleX, scaleY = currentProps.scaleY),
                        children = sharedElementComposable
                    )
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
    children: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .animatable(tag, SharedElementComposable withValue children) + modifier,
        children = children
    )
}
