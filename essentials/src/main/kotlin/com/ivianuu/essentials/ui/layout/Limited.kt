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
import androidx.ui.core.Constraints
import androidx.ui.core.DensityScope
import androidx.ui.core.Dp
import androidx.ui.core.LayoutModifier
import androidx.ui.core.hasBoundedHeight
import androidx.ui.core.hasBoundedWidth

@Immutable
data class LayoutLimited(
    val maxWidth: Dp = Dp.Infinity,
    val maxHeight: Dp = Dp.Infinity
) : LayoutModifier {
    override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
        return if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) constraints
        else constraints.copy(
            maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else maxWidth.toIntPx(),
            maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else maxHeight.toIntPx()
        )
    }
}
