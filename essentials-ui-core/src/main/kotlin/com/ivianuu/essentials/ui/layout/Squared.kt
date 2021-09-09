/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrain

fun Modifier.squared(fit: SquareFit) = this.then(SquaredModifier(fit))

private data class SquaredModifier(val fit: SquareFit) : LayoutModifier {
  override fun MeasureScope.measure(
    measurable: Measurable,
    constraints: Constraints
  ): MeasureResult {
    val size = when (fit) {
      SquareFit.FIT_WIDTH -> constraints.maxWidth
      SquareFit.FIT_HEIGHT -> constraints.maxHeight
    }
    val finalConstraints = constraints.copy(
      maxWidth = size,
      maxHeight = size
    ).constrain(constraints)

    val placeable = measurable.measure(finalConstraints)

    return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
  }
}

enum class SquareFit {
  FIT_WIDTH, FIT_HEIGHT
}
