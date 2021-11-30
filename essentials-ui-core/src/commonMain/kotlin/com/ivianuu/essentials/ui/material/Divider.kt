/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

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
