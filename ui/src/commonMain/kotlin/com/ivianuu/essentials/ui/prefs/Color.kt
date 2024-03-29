/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.material.*

@Composable fun ColorListItem(
  value: Color,
  onValueChangeRequest: () -> Unit,
  modifier: Modifier = Modifier,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
) {
  ListItem(
    modifier = modifier,
    onClick = onValueChangeRequest,
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
