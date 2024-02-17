/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.ui.util.*

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
