/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class MultiChoiceListKey<T : Any>(
  val items: List<Item<T>>,
  val selectedItems: Set<T>,
  val title: String? = null
) : PopupKey<Set<T>> {
  data class Item<T>(val value: T, val title: String)
}

@Provide fun multiChoiceListUi(
  key: MultiChoiceListKey<Any>,
  navigator: Navigator,
  strings: CommonStrings
) = KeyUi<MultiChoiceListKey<Any>> {
  DialogScaffold {
    var selectedItems by remember { mutableStateOf(key.selectedItems) }

    MultiChoiceListDialog(
      items = remember {
        key.items
          .map { it.value }
      },
      selectedItems = selectedItems,
      onSelectionsChanged = { selectedItems = it },
      item = { item ->
        Text(key.items.single { it.value == item }.title)
      },
      title = key.title?.let { { Text(it) } },
      buttons = {
        val scope = rememberCoroutineScope()
        TextButton(onClick = {
          scope.launch { navigator.pop(key, null) }
        }) {
          Text(strings.cancel)
        }

        TextButton(
          onClick = {
            scope.launch { navigator.pop(key, selectedItems) }
          }
        ) { Text(strings.ok) }
      }
    )
  }
}
