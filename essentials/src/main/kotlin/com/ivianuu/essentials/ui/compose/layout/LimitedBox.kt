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
import androidx.ui.core.Dp
import androidx.ui.core.PxPosition
import androidx.ui.core.hasBoundedHeight
import androidx.ui.core.hasBoundedWidth
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.withDensity

@Composable
fun LimitedBox(
    maxWidth: Dp = Dp.Infinity,
    maxHeight: Dp = Dp.Infinity,
    child: @Composable() () -> Unit
) = composable("LimitedBox") {
    val maxWidthPx = withDensity { maxWidth.toIntPx() }
    val maxHeightPx = withDensity { maxHeight.toIntPx() }

    SingleChildLayout(child = child) { measureable, constraints ->
        val limitedConstraints = constraints.copy(
            maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else maxWidthPx,
            maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else maxHeightPx
        )

        val placeable = measureable?.measure(limitedConstraints)

        layout(limitedConstraints.maxWidth, limitedConstraints.maxHeight) {
            placeable?.place(PxPosition.Origin)
        }
    }
}