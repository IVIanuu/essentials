/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp

@Composable fun BaseDialog(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = modifier.widthIn(min = 280.dp, max = 356.dp),
    color = MaterialTheme.colors.surface,
    elevation = 24.dp,
    shape = MaterialTheme.shapes.medium,
    content = content
  )
}

