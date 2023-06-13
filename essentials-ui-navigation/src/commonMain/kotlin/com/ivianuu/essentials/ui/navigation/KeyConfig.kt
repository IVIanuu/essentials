/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ui.animation.transition.StackTransition

data class KeyConfig(
  val enterTransition: StackTransition? = null,
  val exitTransition: StackTransition? = null,
  val opaque: Boolean = false,
) {
  constructor(
    transition: StackTransition,
    opaque: Boolean = false,
  ) : this(transition, transition, opaque)
}

fun interface KeyConfigFactory<K : Key<*>> {
  operator fun invoke(navigator: Navigator, scope: Scope<KeyUiScope>, key: K): KeyConfig
}

fun <K : Key<*>> noOpKeyConfigFactory() = KeyConfigFactory<K> { _, _, _ -> defaultKeyConfig }

private val defaultKeyConfig = KeyConfig()
