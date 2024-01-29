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
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable fun HorizontalDivider(
  modifier: Modifier = Modifier,
  color: Color = LocalContentColor.current.copy(alpha = 0.12f),
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(color)
      .height(1.dp)
      .then(modifier)
  )
}

@Composable fun VerticalDivider(
  modifier: Modifier = Modifier,
  color: Color = LocalContentColor.current.copy(alpha = 0.12f),
) {
  Box(
    modifier = Modifier
      .fillMaxHeight()
      .background(color)
      .width(1.dp)
      .then(modifier)
  )
}
