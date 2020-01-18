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
import androidx.ui.unit.IntPx
import androidx.ui.unit.PxPosition

@Composable
fun WithModifier(
    modifier: Modifier,
    children: @Composable() () -> Unit
) {
    Layout(children = children, modifier = modifier) { measureables, constraints ->
        val placeables = measureables.map { it.measure(constraints) }
        val width = placeables.maxBy { it.width.value }?.width ?: IntPx.Zero
        val height = placeables.maxBy { it.height.value }?.height ?: IntPx.Zero
        layout(width = width, height = height) {
            placeables.forEach { placeable ->
                placeable.place(PxPosition.Origin)
            }
        }
    }
}
