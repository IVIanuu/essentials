/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

// todo make fun interface once compose is fixed
@Stable interface Ui<S : Screen<*>, M> {
  @Composable operator fun invoke(model: M)
}

inline fun <S : Screen<*>, M> Ui(
  crossinline block: @Composable (M) -> Unit
): Ui<S, M> = object : Ui<S, M> {
  @Composable override fun invoke(model: M) {
    block(model)
  }
}
