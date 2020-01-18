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
import androidx.ui.core.LayoutNode
import androidx.ui.core.Modifier
import androidx.ui.core.ParentData
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.visitLayoutChildren
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.max
import androidx.ui.unit.px
import androidx.ui.unit.round
import androidx.ui.unit.toPx
import androidx.ui.unit.withDensity
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.Async
import com.ivianuu.essentials.ui.common.FullScreenLoading
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.util.Async

@Composable
fun <T> AsyncList3(
    state: Async<List<T>>,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    successEmpty: @Composable() () -> Unit = {},
    successItem: @Composable() (Int, T) -> ScrollableListItem3
) {
    Async(
        state = state,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            if (items.isNotEmpty()) {
                ScrollableList3(
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
fun ScrollableList3(
    items: List<ScrollableListItem3>,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    modifier: Modifier = Modifier.None,
    enabled: Boolean = true
) {
    val density = ambientDensity()
    // todo make this a param
    val state = remember(position) { ScrollableListState3(density, position) }

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
                    ScrollableListLayout3(
                        state = state,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun Item3(item: ScrollableListItem3) {
    if (item.shouldCompose) {
        d { "lifecycle: compose ${item.key}" }
        ParentData(item) {
            RepaintBoundary {
                Draw(
                    children = item.children,
                    onPaint = { canvas, parentSize ->
                        d { "draw item ${item.key} ${item.shouldDraw}" }
                        if (item.shouldDraw) {
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
    }
}

//@Immutable
data class ScrollableListItem3(
    val key: Any,
    val size: Dp,
    val children: @Composable() () -> Unit
) {
    internal var leading = Px.Zero
    internal var trailing = Px.Zero
    internal var shouldDraw by framed(false)
    internal var shouldCompose by framed(false)
    internal var shouldMeasure by framed(false)

    internal var needsRemeasure = true

    override fun toString(): String = key.toString()
}

@Stable
private class ScrollableListState3(
    val density: Density,
    val position: ScrollPosition
) {

    val items = mutableListOf<ScrollableListItem3>()

    var viewportSize = (-1).px
        set(value) {
            field = value
            updateBounds()
        }

    private var totalSize = Px.Zero

    fun setItems(items: List<ScrollableListItem3>) {
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
        val start = (position.value - viewportSize).coerceIn(Px.Zero, totalSize)
        val end = (position.value + viewportSize + viewportSize).coerceIn(Px.Zero, totalSize)
        val composeRange = start..end
        val visibleRange = position.value..(position.value + viewportSize)
        items.forEach {
            it.shouldDraw = it.leading in visibleRange || it.trailing in visibleRange
            it.shouldCompose = it.shouldDraw//it.leading in composeRange || it.trailing in composeRange
            it.shouldMeasure = it.shouldCompose
            d { "updated state ${it.key} leading ${it.leading} size ${it.size} traling ${it.trailing} " +
                    "compose: ${it.shouldCompose} draw ${it.shouldDraw} measure ${it.shouldMeasure} " +
                    "visible range $visibleRange compose range $composeRange" }
        }

    }
}

@Composable
private fun ScrollableListLayout3(
    state: ScrollableListState3,
    modifier: Modifier
) {
    state.items.forEach { it.needsRemeasure = true }

    Layout(children = {
        state.items.forEach { item ->
            key(item.key) {
                Item3(item)
            }
        }
    }, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val layoutNode = (this as LayoutNode.InnerMeasureScope).layoutNode

        layoutNode.layoutChildren.mapNotNull { child ->
            val item = child.parentData as ScrollableListItem3
            if (true) {
                var itemNeedsRemeasure = false
                child.visitLayoutChildren {
                    if (it.needsRemeasure) itemNeedsRemeasure = true
                }
                if (itemNeedsRemeasure || item.needsRemeasure) {
                    d { "lifecycle: measure ${item.key}" }
                    child.measure(childConstraints)
                    item.needsRemeasure = false
                } else {
                    child.javaClass.getDeclaredField("needsRemeasure")
                        .also { it.isAccessible = true }
                        .set(child, false)
                }
            } else {
                null
            }
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (state.viewportSize != constraints.maxHeight.toPx()) {
                state.viewportSize = constraints.maxHeight.toPx()
            }

            layoutNode.layoutChildren
                .forEach { child ->
                    val item = child.parentData as ScrollableListItem3
                    if (true) {
                        d { "lifecycle: place ${item.key}" }
                        child.place(IntPx.Zero, (item.leading - state.position.value).round())
                    }
                }
        }
    }
}
