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
import androidx.ui.core.Dp
import androidx.ui.core.Px
import androidx.ui.layout.Column
import com.github.ajalt.timberkt.d

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSize: Dp,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    items = items,
    itemSizeProvider = { itemSize },
    item = item
)

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    itemCount = items.size,
    itemSizeProvider = itemSizeProvider,
    item = { item(it, items[it]) }
)

fun SliverChildren.SliverList(
    itemCount: Int,
    itemSize: Dp,
    item: @Composable() (Int) -> Unit
) = SliverList(
    itemCount = itemCount,
    itemSizeProvider = { itemSize },
    item = item
)

fun SliverChildren.SliverList(
    itemCount: Int,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int) -> Unit
) = Sliver { constraints ->
    var totalItemSize = Px.Zero
    (0 until itemCount)
        .map(itemSizeProvider)
        .map { it.toPx() }
        .forEach { totalItemSize += it }

    content(
        geometry = SliverGeometry(
            scrollSize = totalItemSize,
            paintSize = totalItemSize,
            paintOrigin = -constraints.scrollPosition
        )
    ) {
        Column {
            (0 until itemCount)
                .onEach { d { "sliver item -> $it" } }
                .forEach(item)
        }
    }
}