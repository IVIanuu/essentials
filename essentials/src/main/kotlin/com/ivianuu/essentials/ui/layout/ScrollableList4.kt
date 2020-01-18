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
import androidx.compose.Immutable
import androidx.compose.Observe
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Clip
import androidx.ui.core.Layout
import androidx.ui.core.LayoutNode
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
import androidx.ui.unit.round
import androidx.ui.unit.toPx
import androidx.ui.unit.withDensity
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.Async
import com.ivianuu.essentials.ui.common.FullScreenLoading
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.util.Async

@Composable
fun <T> AsyncList4(
    state: Async<List<T>>,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    successEmpty: @Composable() () -> Unit = {},
    successItem: @Composable() (Int, T) -> ScrollableListItem4
) {
    Async(
        state = state,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            if (items.isNotEmpty()) {
                ScrollableList4(
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
fun ScrollableList4(
    items: List<ScrollableListItem4>,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    modifier: Modifier = Modifier.None,
    enabled: Boolean = true
) {
    val density = ambientDensity()
    // todo make this a param
    val state = remember(position) { ScrollableListState4(density, position) }

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
                    ScrollableListLayout4(
                        state = state,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun Item4(item: ScrollableListItem4) {
    item.dirty = true
    ParentData(item) {
        RepaintBoundary(children = item.children)
    }
}

@Immutable
data class ScrollableListItem4(
    val key: Any,
    val size: Dp,
    val children: @Composable() () -> Unit
) {
    internal var leading = Px.Zero
    internal var trailing = Px.Zero
    internal var dirty = true
    override fun toString(): String = key.toString()
}

@Stable
private class ScrollableListState4(
    val density: Density,
    val position: ScrollPosition
) {

    val items = mutableListOf<ScrollableListItem4>()
    val visibleItems = modelListOf<ScrollableListItem4>()

    var viewportSize = (-1).px
        set(value) {
            field = value
            updateBounds()
        }

    private var totalSize = Px.Zero

    fun setItems(items: List<ScrollableListItem4>) {
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
        val visibleRange = position.value..(position.value + viewportSize)
        visibleItems.clear()
        visibleItems += items.filter {
            it.leading in visibleRange || it.trailing in visibleRange
        }
    }
}

@Composable
private fun ScrollableListLayout4(
    state: ScrollableListState4,
    modifier: Modifier
) {
    Layout(children = {
        state.visibleItems.forEach { item ->
            key(item.key) {
                Item4(item)
            }
        }
    }, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val layoutNode = (this as LayoutNode.InnerMeasureScope).layoutNode

        layoutNode.layoutChildren.forEach { child ->
            val item = child.parentData as ScrollableListItem4
            if (!item.dirty) {
                child.javaClass.getDeclaredField("needsRemeasure")
                    .also { it.isAccessible }
                    .set(child, false)
            }

            d { "try measure ${item.key} needs remeasure -> ${child.needsRemeasure}" }

            child.measure(childConstraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (state.viewportSize != constraints.maxHeight.toPx()) {
                state.viewportSize = constraints.maxHeight.toPx()
            }

            layoutNode.layoutChildren
                .forEach { child ->
                    val item = child.parentData as ScrollableListItem4
                    child.place(IntPx.Zero, (item.leading - state.position.value).round())
                }
        }
    }
}
