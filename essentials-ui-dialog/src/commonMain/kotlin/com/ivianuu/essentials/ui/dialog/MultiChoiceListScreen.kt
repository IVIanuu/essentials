/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.Strings
import com.ivianuu.essentials.Strings_Cancel
import com.ivianuu.essentials.Strings_Ok
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class MultiChoiceListScreen<T : Any>(
  val items: List<T>,
  val selectedItems: Set<T>,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : DialogScreen<Set<T>>

@Provide fun multiChoiceListUi(
  key: MultiChoiceListScreen<Any>,
  navigator: Navigator,
  strings: Strings
) = Ui<MultiChoiceListScreen<Any>, Unit> {
  DialogScaffold {
    var selectedItems by remember { mutableStateOf(key.selectedItems) }

    MultiChoiceListDialog(
      items = key.items,
      selectedItems = selectedItems,
      onSelectionsChanged = { selectedItems = it },
      item = { Text(key.renderable(it)) },
      title = key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(key, null) }) {
          Text(strings[Strings_Cancel])
        }

        TextButton(onClick = action { navigator.pop(key, selectedItems) }) {
          Text(strings[Strings_Ok])
        }
      }
    )
  }
}
