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

package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.compose.Observe
import androidx.compose.Stable
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Clip
import androidx.ui.core.Draw
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.ParentData
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.max
import androidx.ui.unit.px
import androidx.ui.unit.toPx
import androidx.ui.unit.withDensity
import com.ivianuu.essentials.ui.common.Async
import com.ivianuu.essentials.ui.common.FullScreenLoading
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.util.Async

@Composable
fun <T> AsyncList2(
    state: Async<List<T>>,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    successEmpty: @Composable() () -> Unit = {},
    successItem: @Composable() (Int, T) -> ScrollableListItem2
) {
    Async(
        state = state,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            if (items.isNotEmpty()) {
                ScrollableList2(
                    items = items
                        .mapIndexed { index, item -> successItem(index, item) }
                )
            } else {
                successEmpty()
            }
        }
    )
}

@Composable
fun ScrollableList2(
    items: List<ScrollableListItem2>,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    modifier: Modifier = Modifier.None,
    enabled: Boolean = true
) {
    val density = ambientDensity()
    // todo make this a param
    val state = remember(position) { ScrollableListState2(density, position) }

    Observe {
        remember(items) { state.setItems(items) }
    }

    Scrollable(
        position = state.position,
        direction = direction,
        enabled = enabled
    ) {
        Clip(RectangleShape) {
            Container(alignment = Alignment.TopLeft) {
                RepaintBoundary {
                    Observe {
                        remember(state.position.value) {
                            state.updateVisibleItems()
                        }
                    }
                    ScrollableListLayout2(
                        state = state,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun Item2(item: ScrollableListItem2) {
    //d { "compose ${item.key}" }
    ParentData(item) {
        Draw(
            children = item.children,
            onPaint = { canvas, parentSize ->
                //d { "draw item ${item.key} ${item.shouldDraw}" }
                if (item.isVisible) {
                    drawChildren()
                } else {
                    this.javaClass.getDeclaredField("childDrawn")
                        .also { it.isAccessible = true }
                        .set(this, true)
                }
            }
        )
    }
}

//@Immutable
data class ScrollableListItem2(
    val key: Any,
    val size: Dp,
    val children: @Composable() () -> Unit
) {
    internal var leading = Px.Zero
    internal var trailing = Px.Zero
    internal var isVisible by framed(false)

    override fun toString(): String = key.toString()
}

@Stable
private class ScrollableListState2(
    val density: Density,
    val position: ScrollPosition
) {

    val items = mutableListOf<ScrollableListItem2>()

    var viewportSize = (-1).px
        set(value) {
            field = value
            updateBounds()
        }

    private var totalSize = Px.Zero

    fun setItems(items: List<ScrollableListItem2>) {
        this.items.clear()
        this.items += items

        var totalSize = Px.Zero
        items.forEach { item ->
            item.leading = totalSize
            val sizePx = withDensity(density) { item.size.toPx() }
            item.trailing = totalSize + sizePx
            totalSize += sizePx
        }
        this.totalSize = totalSize

        if (viewportSize != (-1).px) {
            updateBounds()
        }
    }

    fun updateBounds() {
        val newMaxValue = max(Px.Zero, totalSize - viewportSize)
        if (position.maxValue != newMaxValue) {
            position.updateBounds(Px.Zero, newMaxValue)
        }
        updateVisibleItems()
    }

    fun updateVisibleItems() {
        val start = (position.value /* - viewportSize * 0.1f*/).coerceIn(Px.Zero, totalSize)
        val end = (position.value + viewportSize /*+ viewportSize * 0.1f*/).coerceIn(Px.Zero, totalSize)
        val visibleRange = start..end
        //d { "visible range $visibleRange" }
        items
            .forEach { item ->
                item.isVisible = item.leading in visibleRange || item.trailing in visibleRange
            }
    }
}

@Composable
private fun ScrollableListLayout2(
    state: ScrollableListState2,
    modifier: Modifier
) {
    Layout(children = {
        //d { "lifecycle: composed ${state.visibleItems.map { it.key }}" }
        state.items.forEach { item ->
            key(item.key) {
                Item2(item)
            }
        }
    }, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val placeables = measureables.map { measureable ->
            measureable.measure(childConstraints) to measureable.parentData as ScrollableListItem2
        }

        //d { "lifecycle: measured ${placeables.map { it.second.key }}" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (state.viewportSize != constraints.maxHeight.toPx()) {
                state.viewportSize = constraints.maxHeight.toPx()
            }

            placeables.forEach { (placeable, item) ->
                //d { "place ${item.key} h ${placeable.height} l layout ${item.leading - state.position.value} l list ${item.leading}" }
                placeable.place(Px.Zero, item.leading - state.position.value)
            }

            //d { "lifecycle: placed ${placeables.map { it.second.key }}" }
        }
    }
}
