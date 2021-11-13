/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.material.ListItem

@Composable fun <T> SingleChoiceListDialog(
  modifier: Modifier = Modifier,
  item: @Composable (T) -> Unit,
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  buttons: @Composable (() -> Unit)? = null,
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
      Box(modifier = Modifier.absorbPointer()) {
        RadioButton(
          selected = selected,
          onClick = null
        )
      }
    }
  )
}
