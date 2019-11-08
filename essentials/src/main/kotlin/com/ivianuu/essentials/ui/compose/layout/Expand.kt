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
import androidx.ui.core.Layout
import androidx.ui.core.ipx
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun ExpandWidth(child: @Composable() () -> Unit) = composable("ExpandWidth") {
    Expand(expandWidth = true, expandHeight = false, child = child)
}

@Composable
fun ExpandHeight(child: @Composable() () -> Unit) = composable("ExpandHeight") {
    Expand(expandWidth = false, expandHeight = true, child = child)
}

@Composable
fun Expand(
    expandWidth: Boolean = true,
    expandHeight: Boolean = true,
    child: @Composable() () -> Unit
) = composable("Expand") {
    Layout(children = child) { measureables, constraints ->
        val measureable = measureables.firstOrNull()
        val placeable = measureable?.measure(
            constraints.copy(
                minWidth = if (expandWidth) constraints.maxWidth else constraints.minWidth,
                minHeight = if (expandHeight) constraints.maxHeight else constraints.maxHeight
            )
        )

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeable?.place(0.ipx, 0.ipx)
        }
    }
}