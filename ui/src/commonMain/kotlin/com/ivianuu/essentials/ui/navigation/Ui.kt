/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable fun interface Ui<S : Screen<*>, T> {
  @Composable operator fun invoke(state: T)
}
