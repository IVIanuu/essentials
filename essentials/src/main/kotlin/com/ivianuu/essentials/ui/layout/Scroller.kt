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
import androidx.ui.core.Alignment
import androidx.ui.core.Clip
import androidx.ui.core.Modifier
import androidx.ui.core.RepaintBoundary
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.max
import androidx.ui.unit.min
import androidx.ui.unit.round
import androidx.ui.unit.toPx
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.common.Scrollable
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain

@Composable
fun Scroller(
    position: ScrollPosition = retain { ScrollPosition() },
    modifier: Modifier = Modifier.None,
    direction: Axis = Axis.Vertical,
    enabled: Boolean = true,
    child: @Composable() () -> Unit
) {
    Scrollable(
        position = position,
        direction = direction,
        enabled = enabled
    ) {
        ScrollerLayout(
            position = position,
            modifier = modifier,
            direction = direction,
            child = child
        )
    }
}

@Composable
private fun ScrollerLayout(
    position: ScrollPosition,
    modifier: Modifier,
    direction: Axis,
    child: @Composable() () -> Unit
) {
    SingleChildLayout(child = {
        Clip(RectangleShape) {
            Container(alignment = Alignment.TopLeft) {
                RepaintBoundary(children = child)
            }
        }
    }, modifier = modifier) { measurable, constraints ->
        val childConstraints = constraints.copy(
            maxHeight = when (direction) {
                Axis.Vertical -> IntPx.Infinity
                Axis.Horizontal -> constraints.maxHeight
            },
            maxWidth = when (direction) {
                Axis.Vertical -> constraints.maxWidth
                Axis.Horizontal -> IntPx.Infinity
            }
        )
        val placeable = measurable?.measure(childConstraints)

        val width: IntPx
        val height: IntPx
        if (placeable == null) {
            width = constraints.minWidth
            height = constraints.minHeight
        } else {
            width = min(placeable.width, constraints.maxWidth)
            height = min(placeable.height, constraints.maxHeight)
        }

        layout(width, height) {
            val newMaxValue = when (direction) {
                Axis.Vertical -> max(
                    Px.Zero,
                    (placeable?.height?.toPx() ?: Px.Zero) - height.toPx()
                )
                Axis.Horizontal -> max(
                    Px.Zero,
                    (placeable?.width?.toPx() ?: Px.Zero) - (width.toPx())
                )
            }

            if (position.maxValue != newMaxValue) {
                position.updateBounds(Px.Zero, newMaxValue)
            }

            val childX = when (direction) {
                Axis.Vertical -> IntPx.Zero
                Axis.Horizontal -> -position.value.round()
            }
            val childY = when (direction) {
                Axis.Vertical -> -position.value.round()
                Axis.Horizontal -> IntPx.Zero
            }
            placeable?.place(childX, childY)
        }
    }
}
