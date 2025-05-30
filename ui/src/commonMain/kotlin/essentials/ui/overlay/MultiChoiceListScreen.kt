/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay

import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class MultiChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: Set<T>,
  val title: String? = null,
  val renderable: UiRenderer<T> = inject
) : OverlayScreen<Set<T>>

@Provide @Composable fun MultiChoiceListUi(
  context: ScreenContext<MultiChoiceListScreen<Any>> = inject,
): Ui<MultiChoiceListScreen<Any>> {
  var selectedItems by remember { mutableStateOf(context.screen.selected) }

  BottomSheet(onDismissRequest = { popWithResult(selectedItems) }) {
    if (context.screen.title != null)
      Subheader { Text(context.screen.title) }
    EsLazyColumn {
      section {
        sectionItems(context.screen.items) { item, _ ->
          val selected = item in selectedItems
          SectionSwitch(
            checked = selected,
            onCheckedChange = {
              val newSelectedItems = selectedItems.toMutableSet()
              if (!selected) newSelectedItems += item
              else newSelectedItems -= item
              selectedItems = newSelectedItems
            },
            title = { Text(context.screen.renderable.render(item)) }
          )
        }
      }
    }
  }
}
