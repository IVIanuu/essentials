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
import com.ivianuu.essentials.ui.compose.core.composable

// todo better name

@Composable
fun WidthFitSquare(child: @Composable() () -> Unit) = composable("WidthFitSquare") {
    FitSquare(type = FitSquareType.ByWidth, child = child)
}

@Composable
fun HeightFitSquare(child: @Composable() () -> Unit) = composable("HeightFitSquare") {
    FitSquare(type = FitSquareType.ByHeight, child = child)
}

@Composable
fun FitSquare(
    type: FitSquareType,
    child: @Composable() () -> Unit
) = composable("FitSquare") {
    SingleChildLayout(child = child) { measureable, constraints ->
        val size = when (type) {
            FitSquareType.ByWidth -> constraints.maxWidth
            FitSquareType.ByHeight -> constraints.maxHeight
        }

        val placeable = if (measureable == null) {
            null
        } else {
            val childConstraints = constraints.copy(
                maxWidth = size,
                maxHeight = size
            )

            measureable.measure(childConstraints)
        }

        layout(width = size, height = size) {
            placeable?.place(IntPx.Zero, IntPx.Zero)
        }
    }
}

enum class FitSquareType {
    ByWidth, ByHeight
}