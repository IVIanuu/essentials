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
import androidx.ui.core.Constraints
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.dp
import androidx.ui.core.enforce
import androidx.ui.core.isFinite
import com.ivianuu.essentials.ui.compose.common.withDensity

@Composable
fun Expand(child: @Composable() () -> Unit) {
    SizedBox(size = Dp.Infinity, child = child)
}

@Composable
fun Shrink(child: @Composable() () -> Unit) {
    SizedBox(size = 0.dp, child = child)
}

@Composable
fun SizedBox(
    size: Dp,
    child: @Composable() () -> Unit
) {
    SizedBox(width = size, height = size, child = child)
}

@Composable
fun SizedBox(
    width: Dp? = null,
    height: Dp? = null,
    child: @Composable() () -> Unit
) {
    val widthPx = withDensity { width?.toIntPx() }
    val heightPx = withDensity { height?.toIntPx() }
    SingleChildLayout(child = child) { measureable, incomingConstraints ->
        if (measureable == null) return@SingleChildLayout layout(
            incomingConstraints.minWidth,
            incomingConstraints.minHeight
        ) {}

        val constraints = Constraints.tightConstraints(
            width = if (width?.isFinite() == true) widthPx!! else incomingConstraints.maxWidth,
            height = if (height?.isFinite() == true) heightPx!! else incomingConstraints.maxHeight
        ).enforce(incomingConstraints)

        val placeable = measureable.measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(
                IntPx.Zero,
                IntPx.Zero
            )
        }
    }
}
