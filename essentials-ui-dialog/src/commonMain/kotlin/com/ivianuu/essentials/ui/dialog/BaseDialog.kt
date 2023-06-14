/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun BaseDialog(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = modifier.widthIn(min = 280.dp, max = 560.dp),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 6.dp,
    shape = MaterialTheme.shapes.medium,
    content = content
  )
}
