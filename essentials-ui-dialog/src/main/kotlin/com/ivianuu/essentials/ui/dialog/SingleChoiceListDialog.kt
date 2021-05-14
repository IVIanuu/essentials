/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.common.*

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
  SimpleDialogListItem(
    leading = {
      Box(modifier = Modifier.absorbPointer()) {
        RadioButton(
          selected = selected,
          onClick = null
        )
      }
    },
    title = title,
    onClick = onSelect
  )
}
