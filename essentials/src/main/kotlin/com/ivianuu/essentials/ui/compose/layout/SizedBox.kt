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
import androidx.ui.core.WithDensity
import androidx.ui.core.coerceIn
import androidx.ui.core.dp
import androidx.ui.core.ipx
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Expand(child: @Composable() () -> Unit) = composable("Expand") {
    SizedBox(size = Dp.Infinity, child = child)
}

@Composable
fun Shrink(child: @Composable() () -> Unit) = composable("Shrink") {
    SizedBox(size = 0.dp, child = child)
}

@Composable
fun SizedBox(
    size: Dp,
    child: @Composable() () -> Unit
) = composable("SizedBox") {
    SizedBox(width = size, height = size, child = child)
}

@Composable
fun SizedBox(
    width: Dp = 0.dp,
    height: Dp = 0.dp,
    child: @Composable() () -> Unit
) = composable("SizedBox") {
    WithDensity {
        SingleChildLayout(child = child) { measureable, constraints ->
            val realWidth = width.toIntPx().coerceIn(constraints.minWidth, constraints.maxWidth)
            val realHeight = height.toIntPx().coerceIn(constraints.minHeight, constraints.maxHeight)
            val placeable =
                measureable?.measure(Constraints(maxWidth = realWidth, maxHeight = realHeight))
            layout(realWidth, realHeight) { placeable?.place(0.ipx, 0.ipx) }
        }
    }
}