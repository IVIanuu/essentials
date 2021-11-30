/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
