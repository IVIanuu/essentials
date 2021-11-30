/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.compose.runtime.*

@Composable fun BackHandler(onBackPress: () -> Unit) {
  val currentOnBack by rememberUpdatedState(onBackPress)

  val handler = LocalBackPressHandler.current
  DisposableEffect(handler) {
    val disposable = handler.registerCallback {
      currentOnBack()
    }
    onDispose { disposable.dispose() }
  }
}
