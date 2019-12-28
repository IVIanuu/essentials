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
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Clip
import androidx.ui.core.Density
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.ParentData
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.max
import androidx.ui.core.px
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
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
fun <T> AsyncList5(
    state: Async<List<T>>,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    successEmpty: @Composable() () -> Unit = {},
    successItem: @Composable() (Int, T) -> ScrollableListItem5
) {
    Async(
        state = state,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            if (items.isNotEmpty()) {
                ScrollableList5(
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
fun ScrollableList5(
    items: List<ScrollableListItem5>,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    modifier: Modifier = Modifier.None,
    enabled: Boolean = true
) {
    val density = ambientDensity()
    // todo make this a param
    val state = remember(position) { ScrollableListState5(density, position) }

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
                            state.updatePage()
                        }
                    }
                    ScrollableListLayout5(
                        state = state,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun Item5(item: ScrollableListItem5) {
    ParentData(item) {
        RepaintBoundary(children = item.children)
    }
}

@Immutable
data class ScrollableListItem5(
    val key: Any,
    val size: Dp,
    val children: @Composable() () -> Unit
) {
    internal var leading = Px.Zero
    internal var trailing = Px.Zero
    override fun toString(): String = key.toString()
}

private val emptyRange = (-1.px)..(-1.px)

@Stable
private class ScrollableListState5(
    val density: Density,
    val position: ScrollPosition
) {

    var currentPage by framed(Page(emptyList(), emptyRange, emptyRange))

    private val allItems = mutableListOf<ScrollableListItem5>()

    var viewportSize = (-1).px
        set(value) {
            field = value
            updateBounds()
        }

    private var totalSize = Px.Zero

    fun setItems(items: List<ScrollableListItem5>) {
        this.allItems.clear()
        this.allItems += items

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

        updatePage()
    }

    fun updatePage() {
        if (allItems.isEmpty()) {
            currentPage = Page(emptyList(), emptyRange, emptyRange)
            return
        }

        if (position.value in currentPage.dirtyBounds) return

        val leading = allItems.first().leading
        val trailing = allItems.last().trailing

        val pageSize = viewportSize * 2
        val pageStart = (position.value - pageSize).coerceIn(leading, trailing)
        val pageEnd = (position.value + viewportSize + pageSize).coerceIn(leading, trailing)
        val pageBounds = pageStart..pageEnd

        val dirtyBoundsSize = pageSize * 0.75f
        val dirtyBoundsStart = (position.value - dirtyBoundsSize).coerceIn(leading, trailing)
        val dirtyBoundsEnd = (position.value + viewportSize + dirtyBoundsSize).coerceIn(leading, trailing)
        val dirtyBounds = dirtyBoundsStart..dirtyBoundsEnd

        val pageItems = allItems.filter {
            it.leading in pageBounds || it.trailing in pageBounds
        }

        currentPage = Page(
            items = pageItems,
            bounds = pageBounds,
            dirtyBounds = dirtyBounds
        )

        d { "scroll pos ${position.value} updated page size $pageBounds dirty bounds $dirtyBounds items ${pageItems.map { it.key }}" }
    }
}

private data class Page(
    val items: List<ScrollableListItem5>,
    val bounds: ClosedRange<Px>,
    val dirtyBounds: ClosedRange<Px>
)

@Composable
private fun ScrollableListLayout5(
    state: ScrollableListState5,
    modifier: Modifier
) {
    Layout(children = {
        state.currentPage.items.forEach { item ->
            key(item.key) {
                Item5(item)
            }
        }
    }, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val placeables = measureables.map { measureable ->
            measureable.measure(childConstraints) to measureable.parentData as ScrollableListItem5
        }

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
