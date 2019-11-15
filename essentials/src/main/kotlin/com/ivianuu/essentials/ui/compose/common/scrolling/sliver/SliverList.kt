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

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import androidx.compose.Composable
import androidx.ui.core.Px
import androidx.ui.core.px
import androidx.ui.core.toPx
import com.github.ajalt.timberkt.d

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSize: Px,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    items = items,
    itemSizeProvider = { _, _ -> itemSize },
    item = item
)

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSizeProvider: (Int, SliverConstraints) -> Px,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    count = items.size,
    itemSizeProvider = itemSizeProvider,
    item = { item(it, items[it]) }
)

fun SliverChildren.SliverList(
    count: Int,
    itemSize: Px,
    item: @Composable() (Int) -> Unit
) = SliverList(
    count = count,
    itemSizeProvider = { _, _ -> itemSize },
    item = item
)

fun SliverChildren.SliverList(
    count: Int,
    itemSizeProvider: (Int, SliverConstraints) -> Px,
    item: @Composable() (Int) -> Unit
) {
    Sliver(children = {
        (0 until count).forEach { item(it) }
    }) { measureables, constraints ->
        if (count == 0) return@Sliver layout(SliverGeometry()) {}

        d { "measuring $constraints" }

        val childConstraints = constraints.asConstraints()

        var offset = Px.Zero
        val placeables = measureables.map {
            val placeable = it.measure(childConstraints)
            val result = placeable to offset
            offset += result.first.height.toPx()
            result
        }
        val totalScrollSize = placeables.sumBy { it.first.height.value }.px

        val geometry = SliverGeometry(
            scrollSize = totalScrollSize,
            paintSize = totalScrollSize,
            maxPaintSize = totalScrollSize
        )

        layout(geometry = geometry) {
            placeables.forEach { (placeable, offset) ->
                d { "place $placeable with offset $offset scroll ${constraints.scrollPosition}" }
                placeable.place(Px.Zero, offset - constraints.scrollPosition)
            }
        }
    }
}