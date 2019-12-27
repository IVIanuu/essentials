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
import androidx.compose.frames.modelListOf
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
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.Stable
import com.ivianuu.essentials.ui.core.retain

@Composable
fun ScrollableList(
    items: List<ScrollableListItem>,
    direction: Axis = Axis.Vertical,
    position: ScrollPosition = retain { ScrollPosition() },
    modifier: Modifier = Modifier.None,
    enabled: Boolean = true
) {
    val density = ambientDensity()
    // todo make this a param
    val state = remember(position) { ScrollableListState(density, position) }

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
                    ScrollableListLayout(
                        state = state,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun Item(item: ScrollableListItem) {
    d { "compose ${item.key}" }
    ParentData(item, item.children)
}

@Immutable
data class ScrollableListItem(
    val key: Any,
    val size: Dp,
    val children: @Composable() () -> Unit
) {
    internal var leading = Px.Zero
    internal var trailing = Px.Zero

    override fun toString(): String = key.toString()
}

@Stable
private class ScrollableListState(
    val density: Density,
    val position: ScrollPosition
) {

    val items = mutableListOf<ScrollableListItem>()
    val visibleItems = modelListOf<ScrollableListItem>()

    var viewportSize = (-1).px
        set(value) {
            field = value
            updateBounds()
        }

    private var totalSize = Px.Zero

    fun setItems(items: List<ScrollableListItem>) {
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
        d { "visible range $visibleRange" }
        val newVisibleItems = items
            .filter { item -> item.leading in visibleRange || item.trailing in visibleRange }
        if (newVisibleItems != visibleItems) {
            visibleItems.clear()
            visibleItems += newVisibleItems
        }
    }
}

@Composable
private fun ScrollableListLayout(
    state: ScrollableListState,
    modifier: Modifier
) {
    Layout(children = {
        d { "lifecycle: composed ${state.visibleItems.map { it.key }}" }
        state.visibleItems.forEach { item ->
            key(item.key) {
                Item(item)
            }
        }
    }, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val placeables = measureables.map { measureable ->
            measureable.measure(childConstraints) to measureable.parentData as ScrollableListItem
        }

        d { "lifecycle: measured ${placeables.map { it.second.key }}" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (state.viewportSize != constraints.maxHeight.toPx()) {
                state.viewportSize = constraints.maxHeight.toPx()
            }

            placeables.forEach { (placeable, item) ->
                d { "place ${item.key} h ${placeable.height} l layout ${item.leading - state.position.value} l list ${item.leading}" }
                placeable.place(Px.Zero, item.leading - state.position.value)
            }

            d { "lifecycle: placed ${placeables.map { it.second.key }}" }
        }
    }
}
