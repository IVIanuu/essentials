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

@Provide fun singleChoiceListUi(
  ctx: KeyUiContext<SingleChoiceListKey<Any>>,
  commonStrings: CommonStrings
) = SimpleKeyUi<SingleChoiceListKey<Any>> {
  DialogScaffold {
    SingleChoiceListDialog(
      items = ctx.key.items,
      selectedItem = ctx.key.selectedItem,
      onSelectionChanged = action { item -> ctx.navigator.pop(ctx.key, item) },
      item = { ctx.key.renderable.toUiString(it) },
      title = ctx.key.title?.let { { Text(it) } },
      buttons = {
        TextButton(onClick = action { ctx.navigator.pop(ctx.key, null) }) {
          Text(commonStrings.cancel)
        }
      }
    )
  }
}
