/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.material.ListItem

@Composable fun ColorListItem(
  value: Color,
  onValueChangeRequest: () -> Unit,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  ListItem(
    modifier = modifier.clickable(onClick = onValueChangeRequest),
    title = title,
    subtitle = subtitle,
    leading = leading,
    trailing = {
      Surface(
        modifier = Modifier.requiredSize(48.dp),
        color = value,
        shape = CircleShape,
        border = BorderStroke(
          width = 1.dp,
          color = LocalContentColor.current
        )
      ) {}
    }
  )
}
