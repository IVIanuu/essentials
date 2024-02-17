/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.material.*

@Composable fun SwitchListItem(
  value: Boolean,
  onValueChange: (Boolean) -> Unit,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  ListItem(
    modifier = modifier,
    onClick = { onValueChange(!value) },
    title = title,
    subtitle = subtitle,
    leading = leading,
    trailing = { Switch(checked = value, onCheckedChange = null) }
  )
}
