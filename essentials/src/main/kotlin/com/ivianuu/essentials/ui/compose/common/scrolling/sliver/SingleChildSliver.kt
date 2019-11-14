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
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Constraints
import androidx.ui.core.Direction
import androidx.ui.core.IntPx
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.px
import androidx.ui.core.toPx
import com.ivianuu.essentials.ui.compose.layout.NonNullSingleChildLayout

fun SliverChildren.SingleChildSliver(
    child: @Composable() () -> Unit
) = Sliver { constraints ->
    val (childSize, setChildSize) = +state { unknownSize }

    val geometry = SliverGeometry(
        scrollSize = if (childSize == unknownSize) Px.Zero else childSize,
        paintSize = if (childSize == unknownSize) Px.Zero else childSize,
        maxPaintSize = if (childSize == unknownSize) Px.Zero else childSize
    )

    content(geometry = geometry) {
        NonNullSingleChildLayout(child = child) { measurable, incomingConstraints ->
            if (childSize == unknownSize) {
                val unconstrainedPlaceable = measurable.measure(Constraints())
                setChildSize(
                    when (constraints.mainAxisDirection) {
                        Direction.LEFT, Direction.RIGHT -> unconstrainedPlaceable.width.toPx()
                        Direction.UP, Direction.DOWN -> unconstrainedPlaceable.height.toPx()
                    }
                )
                layout(IntPx.Zero, IntPx.Zero) {}
            } else {
                val placeable = measurable.measure(incomingConstraints)

                val newChildSize = when (constraints.mainAxisDirection) {
                    Direction.LEFT, Direction.RIGHT -> placeable.width.toPx()
                    Direction.UP, Direction.DOWN -> placeable.height.toPx()
                }

                if (childSize != newChildSize) {
                    setChildSize(newChildSize)
                }

                layout(placeable.width, placeable.height) {
                    placeable.place(PxPosition.Origin)
                }
            }
        }
    }
}

private val unknownSize = (-1).px