/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
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
  ctx: KeyUiContext<MultiChoiceListKey<Any>>,
  commonStrings: CommonStrings
) = SimpleKeyUi<MultiChoiceListKey<Any>> {
  DialogScaffold {
    var selectedItems by remember { mutableStateOf(ctx.key.selectedItems) }

    MultiChoiceListDialog(
      items = ctx.key.items,
      selectedItems = selectedItems,
      onSelectionsChanged = { selectedItems = it },
      item = { Text(ctx.key.renderable(it)) },
      title = ctx.key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { ctx.navigator.pop(ctx.key, null) }) {
          Text(commonStrings.cancel)
        }

        TextButton(onClick = action { ctx.navigator.pop(ctx.key, selectedItems) }) {
          Text(commonStrings.ok)
        }
      }
    )
  }
}
