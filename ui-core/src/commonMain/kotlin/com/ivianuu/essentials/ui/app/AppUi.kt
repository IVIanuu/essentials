/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.app

import androidx.compose.runtime.Composable

fun interface AppUi {
  @Composable operator fun invoke()
}
