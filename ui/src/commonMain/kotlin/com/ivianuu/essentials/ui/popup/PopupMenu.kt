/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*

@Composable fun PopupMenuItem(
  onSelected: () -> Unit,
  enabled: Boolean = true,
  content: @Composable () -> Unit,
) {
  val navigator = LocalScope.current.navigator
  val screen = LocalScope.current.screen

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .interactive(enabled)
      .alpha(if (enabled) 1f else ContentAlpha.disabled)
      .clickable(onClick = action {
        navigator.pop(screen)
        onSelected()
      })
      .padding(start = 16.dp, end = 16.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    content()
  }
}
