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
import androidx.ui.core.Constraints
import androidx.ui.core.IntPxSize
import androidx.ui.core.constrain
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun FractionallySizedBox(
    widthFactor: Float? = null,
    heightFactor: Float? = null,
    alignment: Alignment = Alignment.Center,
    child: @Composable() () -> Unit
) = composable {
    SingleChildLayout(child = child) { measurable, constraints ->
        if (measurable == null) return@SingleChildLayout layout(
            constraints.minWidth, constraints.minHeight
        ) {}

        var (minWidth, maxWidth, minHeight, maxHeight) = constraints

        if (widthFactor != null) {
            val width = maxWidth * widthFactor
            minWidth = width
            maxWidth = width
        }

        if (heightFactor != null) {
            val height = maxHeight * heightFactor
            minHeight = height
            maxHeight = height
        }

        val childConstraints = Constraints(minWidth, maxWidth, minHeight, maxHeight)
        val placeable = measurable.measure(childConstraints)

        val size = constraints.constrain(IntPxSize(placeable.width, placeable.height))

        layout(size.width, size.height) {
            val position = alignment.align(
                IntPxSize(size.width - placeable.width, size.height - placeable.height)
            )
            placeable.place(position.x, position.y)
        }
    }
}
