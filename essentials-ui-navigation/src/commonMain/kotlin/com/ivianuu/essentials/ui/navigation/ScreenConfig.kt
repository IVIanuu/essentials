/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

data class ScreenConfig<K : Screen<*>>(
  val enterTransition: EnterTransition? = null,
  val exitTransition: ExitTransition? = null,
  val opaque: Boolean = false,
)
