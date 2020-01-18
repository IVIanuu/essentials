package com.ivianuu.essentials.ui.core

import androidx.ui.core.Constraints
import androidx.ui.core.hasBoundedHeight
import androidx.ui.core.hasBoundedWidth
import androidx.ui.unit.IntPx
import androidx.ui.unit.ipx

fun Constraints.withTight(width: IntPx? = null, height: IntPx? = null) = Constraints(
    minWidth = width ?: this.minWidth,
    maxWidth = width ?: this.maxWidth,
    minHeight = height ?: this.minHeight,
    maxHeight = height ?: this.maxHeight
)

fun Constraints.looseMin() = this.copy(minWidth = 0.ipx, minHeight = 0.ipx)

fun Constraints.looseMax() = this.copy(maxWidth = IntPx.Infinity, maxHeight = IntPx.Infinity)

fun Constraints.tightMin() = this.withTight(width = minWidth, height = minHeight)

fun Constraints.tightMax() = this.copy(
    minWidth = if (hasBoundedWidth) maxWidth else minWidth,
    minHeight = if (hasBoundedHeight) maxHeight else minHeight
)