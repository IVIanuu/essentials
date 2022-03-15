/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

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
) = SimpleKeyUi<MultiChoiceListKey<Any>> {
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
