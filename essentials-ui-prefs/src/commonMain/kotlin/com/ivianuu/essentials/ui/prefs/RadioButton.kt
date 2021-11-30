/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.clickable
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.material.ListItem

@Composable fun RadioButtonListItem(
  value: Boolean,
  onValueChange: (Boolean) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  ListItem(
    modifier = modifier.clickable { onValueChange(!value) },
    title = title,
    subtitle = subtitle,
    leading = leading,
    trailing = {
      RadioButton(
        selected = value,
        onClick = null
      )
    }
  )
}
