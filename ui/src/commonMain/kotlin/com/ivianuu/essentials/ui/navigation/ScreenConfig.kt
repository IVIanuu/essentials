/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.animation.*

data class ScreenConfig<T : Screen<*>>(
  val enterTransitionSpec: (ElementTransitionSpec<Screen<*>>)? = null,
  val exitTransitionSpec: (ElementTransitionSpec<Screen<*>>)? = null,
  val opaque: Boolean = false,
) {
  constructor(
    opaque: Boolean = false,
    transitionSpec: ElementTransitionSpec<Screen<*>>
  ) : this(transitionSpec, transitionSpec, opaque)
}
