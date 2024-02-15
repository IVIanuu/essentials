/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class SingleChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: T,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : DialogScreen<T>

@Provide fun singleChoiceListUi(
  commonStrings: CommonStrings,
  navigator: Navigator,
  screen: SingleChoiceListScreen<Any>,
) = Ui<SingleChoiceListScreen<Any>, Unit> {
  DialogScaffold {
    Dialog(
      applyContentPadding = false,
      title = screen.title?.let { { Text(it) } },
      content = {
        LazyColumn {
          items(screen.items) { item ->
            ListItem(
              modifier = Modifier.clickable(onClick = action {
                navigator.pop(screen, item)
              }),
              trailingPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
              title = { Text(screen.renderable(item)) },
              trailing = {
                RadioButton(
                  selected = item == screen.selected,
                  onClick = null
                )
              }
            )
          }
        }
      },
      buttons = {
        TextButton(onClick = action { navigator.pop(screen, null) }) {
          Text(commonStrings.cancel)
        }
      }
    )
  }
}
