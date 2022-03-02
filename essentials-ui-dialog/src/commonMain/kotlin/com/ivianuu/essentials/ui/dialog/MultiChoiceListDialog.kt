/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.material.*

@Composable fun <T> MultiChoiceListDialog(
  modifier: Modifier = Modifier,
  item: @Composable (T) -> Unit,
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  buttons: @Composable (() -> Unit)? = null,
  selectedItems: Set<T>,
  onSelectionsChanged: (Set<T>) -> Unit,
  items: List<T>,
) {
  Dialog(
    modifier = modifier,
    applyContentPadding = false,
    icon = icon,
    title = title,
    content = {
      LazyColumn {
        items(items) { item ->
          MultiChoiceDialogListItem(
            title = { item(item) },
            checked = item in selectedItems,
            onCheckedChange = {
              val newSelectedItems = selectedItems.toMutableSet()
              if (it) {
                newSelectedItems += item
              } else {
                newSelectedItems -= item
              }

              onSelectionsChanged(newSelectedItems)
            }
          )
        }
      }
    },
    buttons = buttons
  )
}

@Composable private fun MultiChoiceDialogListItem(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  title: @Composable () -> Unit
) {
  ListItem(
    modifier = Modifier.clickable { onCheckedChange(!checked) },
    contentPadding = PaddingValues(
      horizontal = 24.dp,
      vertical = 16.dp
    ),
    title = title,
    trailing = {
      Switch(
        checked = checked,
        onCheckedChange = null
      )
    }
  )
}
