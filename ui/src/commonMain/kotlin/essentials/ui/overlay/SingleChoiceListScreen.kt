/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class SingleChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: T,
  val title: String? = null,
  val renderable: UiRenderer<T> = inject
) : OverlayScreen<T>

@Provide @Composable fun SingleChoiceListUi(
  screen: SingleChoiceListScreen<Any>,
  scope: Scope<ScreenScope> = inject
): Ui<SingleChoiceListScreen<Any>> {
  var selected by remember { mutableStateOf(screen.selected) }
  EsModalBottomSheet(onDismissRequest = { navigator().pop(screen, selected) }) {
    if (screen.title != null)
      Subheader { Text(screen.title) }

    screen.items.fastForEachIndexed { index, item ->
      SectionListItem(
        sectionType = sectionTypeOf(index, screen.items.size),
        selected = item == selected,
        onClick = { selected = item },
        title = { Text(screen.renderable.render(item)) }
      )
    }
  }
}
