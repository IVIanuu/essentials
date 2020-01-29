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
import androidx.ui.core.LayoutModifier
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPxPosition
import androidx.ui.unit.IntPxSize
import androidx.ui.unit.dp

@Immutable
object LayoutOffset {

    @Immutable
    data class Absolute(
        val offsetX: Dp = 0.dp,
        val offsetY: Dp = 0.dp
    ) : LayoutModifier {
        override fun Density.modifyPosition(
            childSize: IntPxSize,
            containerSize: IntPxSize
        ): IntPxPosition {
            return IntPxPosition(offsetX.toIntPx(), offsetY.toIntPx())
        }
    }

    @Immutable
    data class Fraction(
        val fractionX: Float = 0f,
        val fractionY: Float = 0f
    ) : LayoutModifier {
        override fun Density.modifyPosition(
            childSize: IntPxSize,
            containerSize: IntPxSize
        ): IntPxPosition {
            return IntPxPosition(
                childSize.width * fractionX,
                childSize.height * fractionY
            )
        }
    }

}
