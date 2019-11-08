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
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ipx
import com.ivianuu.essentials.ui.compose.core.composable

// todo is this worth it?

@Composable
fun TightColumn(
    children: @Composable() () -> Unit
) = composable("TightColumn") {
    Layout(children = children) { measureables, constraints ->
        var childConstraints = constraints
        val placeables = measureables.map {
            val placeable = it.measure(childConstraints)
            childConstraints =
                childConstraints.copy(maxHeight = childConstraints.maxHeight - placeable.height)
            placeable
        }

        val height = placeables.sumBy { it.height.value }.ipx

        layout(width = constraints.maxWidth, height = height) {
            var offsetY = IntPx.Zero
            placeables.forEach {
                it.place(IntPx.Zero, offsetY)
                offsetY += it.height
            }
        }
    }
}