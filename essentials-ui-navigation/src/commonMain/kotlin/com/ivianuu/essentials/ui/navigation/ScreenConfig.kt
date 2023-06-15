/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.animation.ElementTransitionSpec

data class ScreenConfig<K : Screen<*>>(
  val enterTransitionSpec: (ElementTransitionSpec<Screen<*>>)? = null,
  val exitTransitionSpec: (ElementTransitionSpec<Screen<*>>)? = null,
  val opaque: Boolean = false,
) {
  constructor(
    transitionSpec: ElementTransitionSpec<Screen<*>>,
    opaque: Boolean = false
  ) : this(transitionSpec, transitionSpec, opaque)
}
