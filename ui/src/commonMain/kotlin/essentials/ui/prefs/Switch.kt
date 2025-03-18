/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.prefs

import androidx.compose.foundation.interaction.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import essentials.ui.material.*

@Composable fun SwitchListItem(
  value: Boolean,
  onValueChange: (Boolean) -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  first: Boolean = false,
  last: Boolean = false,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
  DecoratedListItem(
    first = first,
    last = last,
    modifier = modifier,
    onClick = { onValueChange(!value) },
    headlineContent = headlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = {
      Switch(
        checked = value,
        onCheckedChange = null,
        interactionSource = interactionSource
      )
    },
    interactionSource = interactionSource
  )
}
