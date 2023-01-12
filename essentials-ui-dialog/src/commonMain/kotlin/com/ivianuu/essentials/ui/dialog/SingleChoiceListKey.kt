/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

data class SingleChoiceListKey<T : Any>(
  val items: List<T>,
  val selectedItem: T,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : PopupKey<T>

context(KeyUiContext<SingleChoiceListKey<Any>>, CommonStrings)
    @Provide fun singleChoiceListUi() = SimpleKeyUi<SingleChoiceListKey<Any>> {
  DialogScaffold {
    SingleChoiceListDialog(
      items = key.items,
      selectedItem = key.selectedItem,
      onSelectionChanged = action { item -> navigator.pop(key, item) },
      item = { item ->
        with(key.renderable) { Text(item.toUiString()) }
      },
      title = key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(key, null) }) {
          Text(cancel)
        }
      }
    )
  }
}
