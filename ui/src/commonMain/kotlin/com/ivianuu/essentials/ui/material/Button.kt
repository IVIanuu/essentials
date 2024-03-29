/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable fun Button(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  elevation: ButtonElevation? = null,
  shape: Shape = MaterialTheme.shapes.small,
  border: BorderStroke? = null,
  colors: ButtonColors = ButtonDefaults.esButtonColors(),
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  forceMinSize: Boolean = true,
  content: @Composable RowScope.() -> Unit
) {
  Button(
    onClick,
    modifier
      .then(
        if (forceMinSize) Modifier.sizeIn(minWidth = 96.dp, minHeight = 48.dp)
        else Modifier
      ),
    enabled,
    interactionSource,
    elevation,
    shape,
    border,
    colors,
    contentPadding,
    content
  )
}

@Composable fun OutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  elevation: ButtonElevation? = null,
  shape: Shape = MaterialTheme.shapes.small,
  border: BorderStroke? = ButtonDefaults.outlinedBorder,
  colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  forceMinSize: Boolean = true,
  content: @Composable RowScope.() -> Unit
) = Button(
  onClick = onClick,
  modifier = modifier,
  enabled = enabled,
  interactionSource = interactionSource,
  elevation = elevation,
  shape = shape,
  border = border,
  colors = colors,
  contentPadding = contentPadding,
  forceMinSize = forceMinSize,
  content = content
)

@Composable fun TextButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  elevation: ButtonElevation? = null,
  shape: Shape = MaterialTheme.shapes.small,
  border: BorderStroke? = null,
  colors: ButtonColors = ButtonDefaults.textButtonColors(),
  contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
  forceMinSize: Boolean = true,
  content: @Composable RowScope.() -> Unit
) = Button(
  onClick = onClick,
  modifier = modifier,
  enabled = enabled,
  interactionSource = interactionSource,
  elevation = elevation,
  shape = shape,
  border = border,
  colors = colors,
  contentPadding = contentPadding,
  forceMinSize = forceMinSize,
  content = content
)

@Composable fun ButtonDefaults.esButtonColors(
  backgroundColor: Color = MaterialTheme.colors.secondary,
  contentColor: Color = guessingContentColorFor(backgroundColor)
) = buttonColors(
  backgroundColor = backgroundColor,
  contentColor = contentColor
)
