/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable fun Button(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = MaterialTheme.shapes.small,
  colors: ButtonColors = ButtonDefaults.esButtonColors(),
  elevation: ButtonElevation? = null,
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  forceMinSize: Boolean = true,
  content: @Composable RowScope.() -> Unit
) {
  androidx.compose.material3.Button(
    onClick,
    modifier
      .then(
        if (forceMinSize) Modifier.sizeIn(minWidth = 96.dp, minHeight = 48.dp)
        else Modifier
      ),
    enabled,
    shape,
    colors,
    elevation,
    border,
    contentPadding,
    interactionSource,
    content
  )
}

@Composable fun OutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = MaterialTheme.shapes.small,
  colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
  elevation: ButtonElevation? = null,
  border: BorderStroke? = ButtonDefaults.outlinedButtonBorder,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  forceMinSize: Boolean = true,
  content: @Composable RowScope.() -> Unit
) = Button(
  onClick,
  modifier,
  enabled,
  shape,
  colors,
  elevation,
  border,
  contentPadding,
  interactionSource,
  forceMinSize,
  content
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
  onClick,
  modifier,
  enabled,
  shape,
  colors,
  elevation,
  border,
  contentPadding,
  interactionSource,
  forceMinSize,
  content
)

@Composable fun ButtonDefaults.esButtonColors(
  containerColor: Color = MaterialTheme.colorScheme.secondary,
  contentColor: Color = guessingContentColorFor(containerColor)
) = buttonColors(
  containerColor = containerColor,
  contentColor = contentColor
)
