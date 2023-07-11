/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.ivianuu.essentials.coroutines.bracket

@Composable fun BackHandler(enabled: Boolean = true, onBackPress: () -> Unit) {
  val currentOnBack by rememberUpdatedState(onBackPress)
  val currentEnabled by rememberUpdatedState(enabled)

  val handler = LocalBackPressHandler.current
  LaunchedEffect(handler) {
    bracket(
      acquire = {
        handler.registerCallback(enabled) {
          currentOnBack()
        }
      },
      use = { handle ->
        snapshotFlow { currentEnabled }
          .collect { handle.updateEnabled(it) }
      },
      release = { handle, _ ->
        handle.dispose()
      }
    )
  }
}
