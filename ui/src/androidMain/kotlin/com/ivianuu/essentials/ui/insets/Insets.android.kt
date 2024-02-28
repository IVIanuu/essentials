/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.insets

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.ivianuu.injekt.*
import kotlin.math.*

@Provide /*actual*/ val windowInsetsProvider = WindowInsetsProvider { content ->
  val systemBarsInsets = WindowInsets.systemBars
  val imeInsets = WindowInsets.imeAnimationTarget
  val density = LocalDensity.current
  val layoutDirection = LocalLayoutDirection.current
  val insets = with(density) {
    Insets(
      left = max(systemBarsInsets.getLeft(density, layoutDirection), imeInsets.getLeft(density, layoutDirection)).toDp(),
      top = max(systemBarsInsets.getTop(density), imeInsets.getTop(density)).toDp(),
      right = max(systemBarsInsets.getRight(density, layoutDirection), imeInsets.getRight(density, layoutDirection)).toDp(),
      bottom = max(systemBarsInsets.getBottom(density), imeInsets.getBottom(density)).toDp(),
    )
  }
  CompositionLocalProvider(LocalInsets provides insets, content = content)
}
