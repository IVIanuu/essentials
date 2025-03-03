/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay

import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.EsListItem
import essentials.ui.navigation.*
import injekt.*

class MultiChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: Set<T>,
  val title: String? = null,
  val renderable: UiRenderer<T> = inject
) : DialogScreen<Set<T>> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: MultiChoiceListScreen<Any>,
    ) = Ui<MultiChoiceListScreen<Any>> {
      var selectedItems by remember { mutableStateOf(screen.selected) }

      Dialog(
        title = screen.title?.let { { Text(it) } },
        applyContentPadding = false,
        content = {
          EsLazyColumn(decorators = emptyList()) {
            items(screen.items) { item ->
              val selected = item in selectedItems
              EsListItem(
                onClick = scopedAction {
                  val newSelectedItems = selectedItems.toMutableSet()
                  if (!selected) newSelectedItems += item
                  else newSelectedItems -= item
                  selectedItems = newSelectedItems
                },
                headlineContent = { Text(screen.renderable.render(item)) },
                trailingContent = {
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
          TextButton(onClick = scopedAction { navigator.pop(screen, null) }) {
            Text("Cancel")
          }
          TextButton(onClick = scopedAction { navigator.pop(screen, selectedItems) }) {
            Text("OK")
          }
        }
      )
    }
  }
}
