/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.backpress.*

@Composable fun BackButton(content: @Composable () -> Unit = { Icon(Icons.Default.ArrowBack) }) {
  val backHandler = LocalBackPressHandler.current
  IconButton(
    onClick = { backHandler.back() },
    content = content
  )
}
