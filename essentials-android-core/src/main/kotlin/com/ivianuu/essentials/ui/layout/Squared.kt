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

import androidx.compose.ui.LayoutModifier
import androidx.compose.ui.Measurable
import androidx.compose.ui.MeasureScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.enforce

fun Modifier.squared(fit: SquareFit) = this + SquaredModifier(fit)

private data class SquaredModifier(val fit: SquareFit) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val size = when (fit) {
            SquareFit.MatchWidth -> constraints.maxWidth
            SquareFit.MatchHeight -> constraints.maxHeight
        }
        val finalConstraints = constraints.copy(
            maxWidth = size,
            maxHeight = size
        ).enforce(constraints)

        val placeable = measurable.measure(finalConstraints)

        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
}

enum class SquareFit {
    MatchWidth, MatchHeight
}
