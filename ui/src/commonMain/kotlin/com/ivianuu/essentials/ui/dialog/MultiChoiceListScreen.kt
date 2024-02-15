/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class MultiChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: Set<T>,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : DialogScreen<Set<T>>

@Provide fun multiChoiceListUi(
  commonStrings: CommonStrings,
  navigator: Navigator,
  screen: MultiChoiceListScreen<Any>,
) = Ui<MultiChoiceListScreen<Any>, Unit> {
  DialogScaffold {
    var selectedItems by remember { mutableStateOf(screen.selected) }

    Dialog(
      applyContentPadding = false,
      title = screen.title?.let { { Text(it) } },
      content = {
        LazyColumn {
          items(screen.items) { item ->
            val selected = item in selectedItems
            ListItem(
              modifier = Modifier.clickable {
                val newSelectedItems = selectedItems.toMutableSet()
                if (!selected) newSelectedItems += item
                else newSelectedItems -= item
                selectedItems = newSelectedItems
              },
              trailingPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
              title = { Text(screen.renderable(item)) },
              trailing = {
                Switch(
                  checked = selected,
                  onCheckedChange = null
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

        TextButton(onClick = action { navigator.pop(screen, selectedItems) }) {
          Text(commonStrings.ok)
        }
      }
    )
  }
}
