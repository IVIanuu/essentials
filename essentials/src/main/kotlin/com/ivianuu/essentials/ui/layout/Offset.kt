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

import androidx.ui.core.DensityScope
import androidx.ui.core.Dp
import androidx.ui.core.IntPxPosition
import androidx.ui.core.IntPxSize
import androidx.ui.core.LayoutModifier
import androidx.ui.core.dp

data class LayoutOffset(
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp
) : LayoutModifier {
    override fun DensityScope.modifyPosition(
        childSize: IntPxSize,
        containerSize: IntPxSize
    ): IntPxPosition {
        return IntPxPosition(offsetX.toIntPx(), offsetY.toIntPx())
    }
}

data class LayoutPercentOffset(
    val percentX: Float = 0f,
    val percentY: Float = 0f
) : LayoutModifier {
    override fun DensityScope.modifyPosition(
        childSize: IntPxSize,
        containerSize: IntPxSize
    ): IntPxPosition {
        return IntPxPosition(
            childSize.width * percentX,
            childSize.height * percentY
        )
    }
}