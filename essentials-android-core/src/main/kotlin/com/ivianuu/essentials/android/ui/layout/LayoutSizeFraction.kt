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

package com.ivianuu.essentials.android.ui.layout

import androidx.compose.Immutable
import androidx.ui.core.Alignment
import androidx.ui.core.Constraints
import androidx.ui.core.LayoutDirection
import androidx.ui.core.LayoutModifier
import androidx.ui.unit.Density
import androidx.ui.unit.IntPxPosition
import androidx.ui.unit.IntPxSize

@Immutable
data class LayoutSizeFraction(
    private val widthFactor: Float? = null,
    private val heightFactor: Float? = null,
    private val alignment: Alignment = Alignment.Center
) : LayoutModifier {
    override fun Density.modifyConstraints(
        constraints: Constraints,
        layoutDirection: LayoutDirection
    ): Constraints {
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

    override fun Density.modifyPosition(
        childSize: IntPxSize,
        containerSize: IntPxSize,
        layoutDirection: LayoutDirection
    ): IntPxPosition {
        return alignment.align(
            IntPxSize(
                containerSize.width - childSize.width,
                containerSize.height - childSize.height
            )
        )
    }
}
