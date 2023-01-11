/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

data class MultiChoiceListKey<T : Any>(
  val items: List<T>,
  val selectedItems: Set<T>,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : PopupKey<Set<T>>

@Provide fun multiChoiceListUi(
  key: MultiChoiceListKey<Any>,
  navigator: Navigator,
  strings: CommonStrings
) = SimpleKeyUi<MultiChoiceListKey<Any>> {
  DialogScaffold {
    var selectedItems by remember { mutableStateOf(key.selectedItems) }

    MultiChoiceListDialog(
      items = key.items,
      selectedItems = selectedItems,
      onSelectionsChanged = { selectedItems = it },
      item = { item ->
        with(key.renderable) { Text(item.toUiString()) }
      },
      title = key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(key, null) }) {
          Text(strings.cancel)
        }

        TextButton(onClick = action { navigator.pop(key, selectedItems) }) {
          Text(strings.ok)
        }
      }
    )
  }
}
