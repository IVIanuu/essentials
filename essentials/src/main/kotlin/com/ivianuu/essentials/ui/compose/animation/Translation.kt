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

package com.ivianuu.essentials.ui.compose.animation

import androidx.compose.Composable
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun FractionalTranslation(
    offsetX: Float,
    offsetY: Float,
    child: @Composable() () -> Unit
) = composable("FractionalTranslation") {
    Layout(children = child) { measureables, constraints ->
        val placeable = measureables.first().measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(placeable.width * offsetX, placeable.height * offsetY)
        }
    }
}

@Composable
fun Translation(
    offsetX: IntPx,
    offsetY: IntPx,
    child: @Composable() () -> Unit
) = composable("Translation") {
    Layout(children = child) { measureables, constraints ->
        val placeable = measureables.first().measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(offsetX, offsetY)
        }
    }
}