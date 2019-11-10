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
import androidx.animation.ValueHolder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ipx
import androidx.ui.foundation.animation.AnchorsFlingConfig
import androidx.ui.foundation.animation.AnimatedFloatDragController
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.lerp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable

// todo is this a good name?

// todo use page instead of item for name

// todo only layout current page with surrounders

@Composable
fun <T> Pager(
    items: List<T>,
    state: PagerState = +memo { PagerState(items.size) },
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int, T) -> Unit
) = composable("Pager") {
    Pager(
        state = state,
        direction = direction
    ) {
        item(it, items[it])
    }
}

// todo use @Model once available
class PagerState(val pageCount: Int) {
    internal var currentScrollPosition: Float by framed(0f)

    private var _maxPosition: Float by framed(Float.MAX_VALUE)
    var maxPosition: Float
        get() = _maxPosition
        set(value) {
            _maxPosition = value
            controller = createController()
        }

    internal val pageSize: Float get() = _maxPosition / pageCount

    internal var controller: AnimatedFloatDragController = createController()
        private set

    fun goTo(page: Int) {
        controller.animatedFloat.animateTo(pageSize * page)
    }

    private fun createController(): AnimatedFloatDragController {
        val pageSize = _maxPosition / pageCount
        val anchors = (0 until pageCount).map { -(it * pageSize) }

        d { "create controller anchors $anchors" }

        return AnimatedFloatDragController(
            animatedFloat = AnimatedFloat(
                valueHolder = PagePositionValueHolder(currentScrollPosition) {
                    currentScrollPosition = it
                }
            ),
            flingConfig = AnchorsFlingConfig(
                anchors = anchors
            )
        )
    }
}

@Composable
fun Pager(
    state: PagerState,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int) -> Unit
) = composable("Pager") {
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
            state,
            direction = direction,
            item = item
        )
    }
}

@Composable
private fun PagerLayout(
    state: PagerState,
    direction: Axis,
    item: @Composable() (Int) -> Unit
) = composable("PagerLayout") {
    Layout({
        (0 until state.pageCount).forEach(item)
    }) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = constraints.maxHeight
        )
        val placeables = measureables.map {
            it.measure(childConstraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val newMaxSize = constraints.maxWidth.value.toFloat() * state.pageCount
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