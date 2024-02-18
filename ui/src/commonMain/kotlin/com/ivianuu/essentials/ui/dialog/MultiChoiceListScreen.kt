/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class MultiChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: Set<T>,
  val title: String? = null,
  @Inject val renderable: UiRenderer<T>
) : DialogScreen<Set<T>> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: MultiChoiceListScreen<Any>,
    ) = Ui<MultiChoiceListScreen<Any>> {
      DialogScaffold {
        var selectedItems by remember { mutableStateOf(screen.selected) }

        Dialog(
          applyContentPadding = false,
          title = screen.title?.let { { Text(it) } },
          content = {
            VerticalList(decorate = false) {
              items(screen.items) { item ->
                val selected = item in selectedItems
                ListItem(
                  onClick = {
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
              Text("Cancel")
            }

            TextButton(onClick = action { navigator.pop(screen, selectedItems) }) {
              Text("OK")
            }
          }
        )
      }
    }
  }
}
