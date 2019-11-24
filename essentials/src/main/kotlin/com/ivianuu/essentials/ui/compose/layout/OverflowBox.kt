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

package com.ivianuu.essentials.ui.compose.layout

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.IntPxSize
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.dp
import androidx.ui.layout.Constraints
import androidx.ui.layout.DpConstraints
@Composable
fun OverflowBox(
    minWidth: Dp = 0.dp,
    maxWidth: Dp = Dp.Infinity,
    minHeight: Dp = 0.dp,
    maxHeight: Dp = Dp.Infinity,
    alignment: Alignment = Alignment.Center,
    child: @Composable() () -> Unit
) {
    OverflowBox(
        constraints = DpConstraints(
            minWidth = minWidth,
            maxWidth = maxWidth,
            minHeight = minHeight,
            maxHeight = maxHeight
        ),
        alignment = alignment,
        child = child
    )
}

@Composable
fun OverflowBox(
    constraints: DpConstraints,
    alignment: Alignment = Alignment.Center,
    child: @Composable() () -> Unit
) {
    SingleChildLayout(child = {
        RepaintBoundary(children = child)
    }) { measurable, incomingConstraints ->
        val childConstraints = Constraints(constraints)
        val placeable = measurable?.measure(childConstraints)
        layout(incomingConstraints.maxWidth, incomingConstraints.maxHeight) {
            if (placeable != null) {
                val position = alignment.align(
                    IntPxSize(
                        incomingConstraints.maxWidth - placeable.width,
                        incomingConstraints.maxHeight - placeable.height
                    )
                )
                placeable.place(position)
            }
        }
    }
}