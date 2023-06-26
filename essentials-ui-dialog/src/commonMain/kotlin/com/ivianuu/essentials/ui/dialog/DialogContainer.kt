/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun DialogContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Surface(
    modifier = modifier
      .widthIn(min = 280.dp, max = 356.dp),
    color = MaterialTheme.colors.surface,
    elevation = 24.dp,
    shape = MaterialTheme.shapes.medium
  ) {
    Box(
      modifier = Modifier.animateContentSize()
    ) { content() }
  }
}
