/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.ui.backpress.LocalBackPressHandler

@Composable fun BackButton(content: @Composable () -> Unit = { Icon(Icons.Default.ArrowBack) }) {
  val backHandler = LocalBackPressHandler.current
  IconButton(
    onClick = { backHandler.back() },
    content = content
  )
}
