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

import androidx.compose.Immutable
import androidx.ui.core.Alignment
import androidx.ui.core.Constraints
import androidx.ui.core.LayoutModifier
import androidx.ui.unit.DensityScope
import androidx.ui.unit.IntPxPosition
import androidx.ui.unit.IntPxSize

@Immutable
data class LayoutFractionalSize(
    val widthFactor: Float? = null,
    val heightFactor: Float? = null,
    val alignment: Alignment = Alignment.Center
) : LayoutModifier {
    override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
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

        return Constraints(minWidth, maxWidth, minHeight, maxHeight)
    }

    override fun DensityScope.modifyPosition(
        childSize: IntPxSize,
        containerSize: IntPxSize
    ): IntPxPosition {
        return alignment.align(childSize) // todo
    }
}
