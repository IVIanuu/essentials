/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.material.*

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

@Composable fun RadioButtonListItem(
  value: Value<Boolean>,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  RadioButtonListItem(
    value = value.current,
    onValueChange = value.updater,
    title = title,
    subtitle = subtitle,
    leading = leading,
    modifier = modifier
  )
}
