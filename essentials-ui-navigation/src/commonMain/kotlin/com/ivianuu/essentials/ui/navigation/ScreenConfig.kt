/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.animation.transition.StackTransition

data class ScreenConfig<K : Screen<*>>(
  val enterTransition: StackTransition? = null,
  val exitTransition: StackTransition? = null,
  val opaque: Boolean = false,
) {
  constructor(
    transition: StackTransition,
    opaque: Boolean = false,
  ) : this(transition, transition, opaque)
}
