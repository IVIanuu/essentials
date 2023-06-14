/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
      LocalTextStyle provides MaterialTheme.typography.bodyLarge,
      LocalContentColor provides MaterialTheme.colorScheme.secondary,
      content = text
    )
  }
}
