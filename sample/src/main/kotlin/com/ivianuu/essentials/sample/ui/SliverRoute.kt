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

package com.ivianuu.essentials.sample.ui

import androidx.compose.frames.modelListOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Direction
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.SliverChildren
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.SliverGeometry
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.SliverList
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.SliverScroller
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.applyGrowthDirection
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.withDensity
import com.ivianuu.essentials.ui.compose.layout.NonNullSingleChildLayout
import com.ivianuu.essentials.ui.compose.material.SimpleListItem


val sliverRoute = composeControllerRoute {
    val indices = +memo {
        modelListOf<Int>().apply {
            (1..100).forEach { this += it }
        }
    }

    indices.size // required to properly update the sliver list

    SliverScroller {
        SliverList(
            items = indices,
            itemSize = +withDensity { 48.dp.toPx() },
            item = { _, item ->
                SimpleListItem(
                    title = { Text("Item $item") },
                    onClick = { indices -= item }
                )
            }
        )
    }
}

private fun SliverChildren.AppBarSliver() = Sliver { constraints ->
    val maxSize = 300.dp.toPx()

    val shrinkOffset = min(constraints.scrollPosition, maxSize)
    val paintSize = max(maxSize - constraints.scrollPosition, Px.Zero)
    val geometry = SliverGeometry(
        scrollSize = maxSize,
        paintSize = paintSize,
        maxPaintSize = paintSize,
        hasVisualOverflow = true
    )

    val childPosition = min(Px.Zero, paintSize - shrinkOffset)

    d { "shrink offset $shrinkOffset paint size $paintSize child position $childPosition" }

    content(geometry = geometry) {
        NonNullSingleChildLayout(child = {
            Surface(color = ((+MaterialTheme.colors()).primary).copy(alpha = 0.5f)) {
                Container(
                    height = paintSize.toDp(),
                    alignment = Alignment.BottomLeft,
                    padding = EdgeInsets(all = 16.dp)
                ) {
                    Text(
                        text = "Hello",
                        style = (+MaterialTheme.typography()).h6
                    )
                }
            }
        }) { measureable, incomingConstraints ->
            val placeable = measureable.measure(incomingConstraints)
            layout(incomingConstraints.minWidth, incomingConstraints.minWidth) {
                var childX = Px.Zero
                var childY = Px.Zero
                if (geometry.visible) {
                    when (constraints.mainAxisDirection.applyGrowthDirection(constraints.growthDirection)) {
                        Direction.LEFT -> {
                            childX = childPosition - paintSize
                        }
                        Direction.UP -> {
                            childY = childPosition - paintSize
                        }
                        Direction.RIGHT -> {
                            childX = childPosition
                        }
                        Direction.DOWN -> {
                            childY = childPosition
                        }
                    }

                    placeable.place(PxPosition.Origin)
                }
            }
        }
    }
}