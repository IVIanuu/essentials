/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

// todo make fun interface once compose is fixed
@Stable interface Ui<S : Screen<*>, T> {
  @Composable operator fun invoke(state: T)
}

inline fun <S : Screen<*>, T> Ui(
  crossinline block: @Composable (T) -> Unit
): Ui<S, T> = object : Ui<S, T> {
  @Composable override fun invoke(state: T) {
    block(state)
  }
}
