/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.consumeGestures
import com.ivianuu.essentials.ui.material.ListItem

@Composable fun <T> SingleChoiceListDialog(
  modifier: Modifier = Modifier,
  item: @Composable (T) -> Unit,
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  buttons: (@Composable () -> Unit)? = null,
  onSelectionChanged: (T) -> Unit,
  items: List<T>,
  selectedItem: T,
) {
  Dialog(
    modifier = modifier,
    applyContentPadding = false,
    icon = icon,
    title = title,
    content = {
      LazyColumn {
        items(items) { item ->
          SingleChoiceDialogListItem(
            title = { item(item) },
            selected = item == selectedItem,
            onSelect = { onSelectionChanged(item) }
          )
        }
      }
    },
    buttons = buttons
  )
}

@Composable private fun SingleChoiceDialogListItem(
  selected: Boolean,
  onSelect: () -> Unit,
  title: @Composable () -> Unit
) {
  ListItem(
    modifier = Modifier.clickable(onClick = onSelect),
    contentPadding = PaddingValues(
      horizontal = 24.dp,
      vertical = 16.dp
    ),
    title = title,
    trailing = {
      Box(modifier = Modifier.consumeGestures()) {
        RadioButton(
          selected = selected,
          onClick = null
        )
      }
    }
  )
}
