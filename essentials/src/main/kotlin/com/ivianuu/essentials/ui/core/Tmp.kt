package com.ivianuu.essentials.ui.core

import androidx.ui.unit.IntPxBounds
import androidx.ui.unit.PxBounds
import androidx.ui.unit.round

fun PxBounds.round() = IntPxBounds(
    left = left.round(),
    top = top.round(),
    right = right.round(),
    bottom = bottom.round()
)