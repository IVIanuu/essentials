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
import androidx.ui.core.Constraints
import androidx.ui.core.LayoutDirection
import androidx.ui.core.LayoutModifier
import androidx.ui.core.enforce
import androidx.ui.unit.Density

@Immutable
data class LayoutSquared(
    val fit: Fit
) : LayoutModifier {

    override fun Density.modifyConstraints(constraints: Constraints, layoutDirection: LayoutDirection): Constraints {
        val size = when (fit) {
            Fit.MatchWidth -> constraints.maxWidth
            Fit.MatchHeight -> constraints.maxHeight
        }
        return constraints.copy(
            maxWidth = size,
            maxHeight = size
        ).enforce(constraints)
    }

    enum class Fit {
        MatchWidth, MatchHeight
    }
}