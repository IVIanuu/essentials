/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.prefs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import essentials.ui.material.*

@Composable fun ColorListItem(
  value: Color,
  onValueChangeRequest: () -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  first: Boolean = false,
  last: Boolean = false,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
) {
  DecoratedListItem(
    first = first,
    last = last,
    onClick = onValueChangeRequest,
    modifier = modifier,
    headlineContent = headlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = {
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
