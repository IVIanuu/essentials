/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable fun HorizontalDivider(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.outlineVariant,
  thickness: Dp = 1.dp
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(color)
      .height(thickness)
      .then(modifier)
  )
}

@Composable fun VerticalDivider(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.outlineVariant,
  thickness: Dp = 1.dp
) {
  Box(
    modifier = Modifier
      .fillMaxHeight()
      .background(color)
      .width(thickness)
      .then(modifier)
  )
}
