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
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.ipx

// todo improve api

@Composable
fun SpacingRow(
    spacing: Dp,
    modifier: Modifier = Modifier.None,
    children: @Composable() () -> Unit
) {
    Layout(children = children, modifier = modifier) { measureables, constraints ->
        var childConstraints = constraints.copy(maxWidth = constraints.maxWidth)
        val placeables = measureables.map { measureable ->
            val placeable = measureable.measure(childConstraints)
            childConstraints = childConstraints.copy(
                maxWidth = childConstraints.maxWidth - placeable.width - spacing.toIntPx()
            )
            placeable
        }

        val height = placeables.maxBy { it.height.value }?.height ?: IntPx.Zero
        val width = placeables.sumBy { it.width.value }.ipx + (spacing.toIntPx() * (placeables.size - 1))

        layout(width = width, height = height) {
            var offsetX = IntPx.Zero
            placeables.forEach { placeable ->
                val y = height / 2 - placeable.height / 2
                placeable.place(offsetX, y)
                offsetX += placeable.width + spacing.toIntPx()
            }
        }
    }
}
