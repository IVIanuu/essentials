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

package com.ivianuu.essentials.ui.compose.common.scrolling

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Clip
import androidx.ui.core.IntPx
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.layout.SingleChildLayout

@Composable
fun Scroller(
    position: ScrollPosition = +memo { ScrollPosition() },
    direction: Axis = Axis.Vertical,
    enabled: Boolean = true,
    reverse: Boolean = false,
    child: @Composable() () -> Unit
) {
    Scrollable(
        position = position,
        direction = direction,
        reverse = reverse,
        enabled = enabled
    ) {
        ScrollerLayout(
            position = position,
            direction = direction,
            child = child
        )
    }
}

@Composable
private fun ScrollerLayout(
    position: ScrollPosition,
    direction: Axis,
    child: @Composable() () -> Unit
) {
    SingleChildLayout(child = {
        Clip(RectangleShape) {
            Container(alignment = Alignment.TopLeft) {
                RepaintBoundary(children = child)
            }
        }
    }) { measurable, constraints ->
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

            d { "placeable height ${placeable?.height}" }

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