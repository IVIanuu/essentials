package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.animation.*
import kotlinx.coroutines.*
import kotlin.time.*

fun SharedElementStackTransition(
    vararg sharedElements: Pair<Any, Any>,
    sharedElementAnimationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    contentTransition: StackTransition = VerticalStackTransition(sharedElementAnimationSpec),
    waitingTimeout: Duration = 200.milliseconds
): StackTransition = {
    val states = sharedElements
        .map { (from, to) ->
            if (isPush) {
                SharedElementState(from, fromElement(from), fromElementModifier(from)) to
                        SharedElementState(to, toElement(to), toElementModifier(to))
            } else {
                SharedElementState(to, fromElement(to), fromElementModifier(to)) to
                        SharedElementState(from, toElement(from), toElementModifier(from))
            }
        }

    // hide "to" content while waiting for shared elements
    val toContentModifier = toElementModifier(ContentAnimationElementKey)
    toContentModifier?.value = Modifier.alpha(0f)

    attachTo()

    // wait until all shared elements has been placed on the screen
    withTimeoutOrNull(waitingTimeout) {
        while (true) {
            delay(1)
            if (states.all { (start, end) ->
                    start.bounds != null &&
                            end.bounds != null
                }) break
        }
    }

    // hide the "real to" elements
    states.forEach { (_, end) ->
        end.modifier?.value = Modifier.alpha(0f)
    }

    // install overlay
    overlay {
        println("compose overlays")
        states
            .filter { it.first.bounds != null && it.second.bounds != null }
            .forEach { (_, endState) ->
                val endBounds = endState.bounds!!
                val currentProps = endState.animatedProps!!
                val sharedElementComposable =
                    endState.element!![SharedElementComposable]!!
                with(LocalDensity.current) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = endBounds.width.toDp(),
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
    // hide the "real from" elements
    states.forEach { (start, _) ->
        start.modifier?.value = Modifier.alpha(0f)
    }
    // show the content
    toContentModifier?.value = Modifier.alpha(1f)

    par(
        {
            contentTransition(
                object : StackTransitionScope by this {
                    override fun attachTo() {
                    }
                    override fun detachFrom() {
                    }
                }
            )
        },
        {
            states
                .filter { it.first.bounds != null && it.second.bounds != null }
                .forEach { (start, end) ->
                    val startBounds = start.bounds!!
                    val endBounds = end.bounds!!
                    val startProps = SharedElementProps(
                        position = Offset(
                            x = startBounds.left + (startBounds.width - endBounds.width) / 2,
                            y = startBounds.top + (startBounds.height - endBounds.height) / 2
                        ),
                        scaleX = startBounds.width / endBounds.width,
                        scaleY = startBounds.height / endBounds.height
                    )
                    val endProps = SharedElementProps(
                        position = Offset(endBounds.left, endBounds.top),
                        scaleX = 1f,
                        scaleY = 1f
                    )
                    start.capturedProps = startProps
                    end.capturedProps = endProps
                }

            animate(sharedElementAnimationSpec) {
                states
                    .filter { it.first.capturedProps != null && it.second.capturedProps != null }
                    .forEach { (start, end) ->
                        end.animatedProps = SharedElementProps(
                            position = lerp(start.capturedProps!!.position, end.capturedProps!!.position, value),
                            scaleX = lerp(start.capturedProps!!.scaleX, end.capturedProps!!.scaleX, value),
                            scaleY = lerp(start.capturedProps!!.scaleY, end.capturedProps!!.scaleY, value)
                        )
                    }
            }
        }
    )

    // show the "real" elements
    states.forEach { (_, end) -> end.modifier?.value = Modifier.alpha(1f) }
}

val SharedElementComposable = AnimationElementPropKey<@Composable () -> Unit>()

private data class SharedElementState(
    val key: Any?,
    val element: AnimationElement?,
    val modifier: MutableState<Modifier>?
) {
    var bounds: Rect? = null
        private set
    var animatedProps: SharedElementProps? by mutableStateOf(null)
    var capturedProps: SharedElementProps? = null
    init {
        updateModifier()
    }
    private fun updateModifier() {
        modifier?.value = Modifier
            .onGloballyPositioned { coords ->
                if (bounds == null) {
                    bounds = coords
                        .takeIf { it.isAttached }
                        ?.boundsInRoot()
                }
            }
    }
}

private data class SharedElementProps(
    val position: Offset,
    val scaleX: Float,
    val scaleY: Float
)

@Composable
fun SharedElement(
    key: Any,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .animationElement(key, SharedElementComposable to content).then(modifier)
    ) {
        content()
    }
}
