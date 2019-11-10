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

package com.ivianuu.essentials.ui.compose.common

import androidx.animation.AnimatedFloat
import androidx.animation.AnimationEndReason
import androidx.animation.ValueHolder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ipx
import androidx.ui.foundation.animation.AnchorsFlingConfig
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.DragValueController
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.lerp
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import kotlin.math.absoluteValue

// todo is this a good name?

// todo use page instead of item for name

// todo only layout current page with surrounders

@Composable
fun <T> Pager(
    items: List<T>,
    state: PagerState = +memo { PagerState(items.size) },
    onPageChanged: ((Int) -> Unit)? = null,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int, T) -> Unit
) = composable("Pager") {
    Pager(
        state = state,
        onPageChanged = onPageChanged,
        direction = direction
    ) {
        item(it, items[it])
    }
}

@Composable
fun Pager(
    state: PagerState,
    onPageChanged: ((Int) -> Unit)? = null,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int) -> Unit
) = composable("Pager") {
    state.onPageChanged = onPageChanged

    Draggable(
        dragDirection = when (direction) {
            Axis.Vertical -> DragDirection.Vertical
            Axis.Horizontal -> DragDirection.Horizontal
        },
        minValue = -state.maxPosition,
        maxValue = 0f,
        valueController = state.controller
    ) {
        PagerLayout(
            state = state,
            direction = direction
        ) {
            (0 until state.pageCount).forEach(item)
        }
    }
}

// todo use @Model once available
// todo rename to PagerController
class PagerState(val pageCount: Int) {

    internal var currentScrollPosition: Float by framed(0f)
    val currentPage: Int get() = (currentScrollPosition.absoluteValue / pageSize).toInt()

    private var _maxPosition: Float by framed(Float.MAX_VALUE)
    var maxPosition: Float
        get() = _maxPosition
        set(value) {
            _maxPosition = value
            updateFlingConfig()
        }

    internal var pageSize = 0f

    internal val controller: DragValueController
        get() = _controller

    internal var onPageChanged: ((Int) -> Unit)? = null
    internal var notifiedPage = 0

    private val anim = AnimatedFloat(PagePositionValueHolder(0f) {
        currentScrollPosition = it
    })

    private var _controller = PagerDragController(anim) { onSettled() }

    fun goTo(page: Int) {
        anim.animateTo(-(pageSize * page), onEnd = { _, _ -> onSettled() })
    }

    private fun updateFlingConfig() {
        val allAnchors = (0 until pageCount).map { -(it * pageSize) }

        val anchors = mutableListOf<Float>()
        allAnchors.lastOrNull { it >= currentScrollPosition }?.let { anchors += it }
        allAnchors.firstOrNull { it <= currentScrollPosition }?.let { anchors += it }

        _controller.flingConfig = AnchorsFlingConfig(
            anchors = anchors
        )
    }

    private fun onSettled() {
        if (notifiedPage != currentPage) {
            notifiedPage = currentPage
            onPageChanged?.invoke(currentPage)
        }
        updateFlingConfig()
    }
}

@Composable
private fun PagerLayout(
    state: PagerState,
    direction: Axis,
    children: @Composable() () -> Unit
) = composable("PagerLayout") {
    Layout(children = children) { measureables, constraints ->

        val childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = constraints.maxHeight
        )
        val placeables = measureables.map {
            it.measure(childConstraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            state.pageSize = constraints.maxWidth.value.toFloat()

            val newMaxSize = constraints.maxWidth.value.toFloat() * (state.pageCount - 1)
            if (state.maxPosition != newMaxSize) {
                state.maxPosition = newMaxSize
            }

            var offset = IntPx.Zero
            placeables.forEachIndexed { index, placeable ->
                when (direction) {
                    Axis.Vertical -> {
                        placeable.place(
                            IntPx.Zero,
                            offset + state.currentScrollPosition.toInt().ipx
                        )

                        offset += placeable.height
                    }
                    Axis.Horizontal -> {
                        placeable.place(
                            offset + state.currentScrollPosition.toInt().ipx,
                            IntPx.Zero
                        )

                        offset += placeable.width
                    }
                }
            }
        }
    }
}

private class PagerDragController(
    val animatedFloat: AnimatedFloat,
    val onFlingEnd: () -> Unit
) : DragValueController {

    var enabled: Boolean = true
    var flingConfig: FlingConfig? = null

    override val currentValue
        get() = animatedFloat.value

    override fun setBounds(min: Float, max: Float) = animatedFloat.setBounds(min, max)

    override fun onDrag(target: Float) {
        if (enabled) animatedFloat.snapTo(target)
    }

    override fun onDragEnd(velocity: Float, onValueSettled: (Float) -> Unit) {
        val flingConfig = flingConfig
        if (flingConfig != null && enabled) {
            val config = flingConfig.copy(
                onAnimationEnd =
                { endReason: AnimationEndReason, value: Float, finalVelocity: Float ->
                    if (endReason != AnimationEndReason.Interrupted) onValueSettled(value)
                    flingConfig.onAnimationEnd?.invoke(endReason, value, finalVelocity)
                    onFlingEnd()
                })
            animatedFloat.fling(config, velocity)
        } else {
            onValueSettled(animatedFloat.value)
            onFlingEnd()
        }
    }
}

private class PagePositionValueHolder(
    var current: Float,
    val onValueChanged: (Float) -> Unit
) : ValueHolder<Float> {
    override val interpolator: (start: Float, end: Float, fraction: Float) -> Float = ::lerp
    override var value: Float
        get() = current
        set(value) {
            current = value
            onValueChanged(value)
        }
}