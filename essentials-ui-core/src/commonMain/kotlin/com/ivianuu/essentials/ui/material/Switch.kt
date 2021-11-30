/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
