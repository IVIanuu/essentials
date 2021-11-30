/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.util.isDark

@Composable fun guessingContentColorFor(color: Color): Color = with(MaterialTheme.colors) {
  when (color) {
    primary -> onPrimary
    primaryVariant -> onPrimary
    secondary -> onSecondary
    secondaryVariant -> onSecondary
    background -> onBackground
    surface -> onSurface
    error -> onError
    else -> if (color.isDark) Color.White else Color.Black
  }
}