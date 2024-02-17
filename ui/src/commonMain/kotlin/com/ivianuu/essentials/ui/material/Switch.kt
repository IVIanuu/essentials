/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable fun Switch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  checkedBackgroundColor: Color = MaterialTheme.colors.secondary,
  uncheckedBackgroundColor: Color = LocalContentColor.current.copy(alpha = 0.54f),
  checkedThumbColor: Color = Color.White.copy(alpha = 0.87f),
  uncheckedThumbColor: Color = guessingContentColorFor(uncheckedBackgroundColor).copy(alpha = 0.87f)
) {
  val transition = updateTransition(checked)

  val thumbX by transition.animateDp {
    if (it) SwitchWidth - ThumbSize - ThumpPadding else ThumpPadding
  }

  val backgroundColor by transition.animateColor {
    if (it) checkedBackgroundColor else uncheckedBackgroundColor
  }

  val thumbColor by transition.animateColor {
    if (it) checkedThumbColor else uncheckedThumbColor
  }

  Box(
    modifier = Modifier
      .requiredSize(SwitchWidth, SwitchHeight)
      .background(backgroundColor, RoundedCornerShape(SwitchHeight / 2))
      .then(
        if (onCheckedChange != null) Modifier.clickable { onCheckedChange(!checked) }
        else Modifier
      ),
    contentAlignment = Alignment.CenterStart
  ) {
    Box(
      modifier = Modifier
        .offset(x = thumbX)
        .requiredSize(ThumbSize)
        .background(thumbColor, CircleShape)
    )
  }
}

private val SwitchWidth = 56.dp
private val SwitchHeight = 28.dp
private val ThumbSize = 20.dp
private val ThumpPadding = 4.dp
