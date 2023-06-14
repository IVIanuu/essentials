/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material3.Text
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class SingleChoiceListScreen<T : Any>(
  val items: List<T>,
  val selectedItem: T,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : OverlayScreen<T>

@Provide fun singleChoiceListUi(
  commonStrings: CommonStrings,
  key: SingleChoiceListScreen<Any>,
  navigator: Navigator
) = Ui<SingleChoiceListScreen<Any>, Unit> {
  DialogScaffold {
    SingleChoiceListDialog(
      items = key.items,
      selectedItem = key.selectedItem,
      onSelectionChanged = action { item -> navigator.pop(key, item) },
      item = { Text(key.renderable(it)) },
      title = key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { navigator.pop(key, null) }) {
          Text(commonStrings.cancel)
        }
      }
    )
  }
}
