/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.remember
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

data class SingleChoiceListKey<T : Any>(
  val items: List<Item<T>>,
  val selectedItem: T,
  val title: String? = null
) : PopupKey<T> {
  data class Item<T : Any>(val value: T, val title: String)
}

@Provide fun singleChoiceListUi(
  key: SingleChoiceListKey<Any>,
  navigator: Navigator,
  strings: CommonStrings
) = SimpleKeyUi<SingleChoiceListKey<Any>> {
  DialogScaffold {
    SingleChoiceListDialog(
      items = remember {
        key.items
          .map { it.value }
      },
      selectedItem = key.selectedItem,
      onSelectionChanged = action { item -> navigator.pop(key, item) },
      item = { item ->
        Text(key.items.single { it.value == item }.title)
      },
      title = key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(key, null) }) {
          Text(strings.cancel)
        }
      }
    )
  }
}
