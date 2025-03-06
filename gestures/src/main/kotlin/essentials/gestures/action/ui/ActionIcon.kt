/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import essentials.gestures.action.*

@Composable fun ActionIcon(action: Action<*>, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier,
    propagateMinConstraints = true,
    contentAlignment = Alignment.Center
  ) {
    action.icon()
  }
}
