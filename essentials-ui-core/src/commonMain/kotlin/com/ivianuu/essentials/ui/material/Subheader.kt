/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable fun Subheader(
  modifier: Modifier = Modifier,
  text: @Composable () -> Unit
) {
  Box(
    modifier = Modifier.height(48.dp)
      .fillMaxWidth()
      .padding(
        start = 16.dp,
        top = 16.dp,
        end = 16.dp
      )
      .then(modifier),
    contentAlignment = Alignment.CenterStart
  ) {
    CompositionLocalProvider(
      LocalTextStyle provides MaterialTheme.typography.body2,
      LocalContentColor provides MaterialTheme.colors.secondary,
      content = text
    )
  }
}
