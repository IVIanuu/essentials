/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
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
  context: ScreenContext<SingleChoiceListScreen<Any>> = inject
): Ui<SingleChoiceListScreen<Any>> {
  var selected by remember { mutableStateOf(context.screen.selected) }
  BottomSheet(onDismissRequest = { popWithResult(selected) }) {
    if (context.screen.title != null)
      Subheader { Text(context.screen.title) }

    context.screen.items.fastForEachIndexed { index, item ->
      SectionListItem(
        sectionType = sectionTypeOf(index, context.screen.items.size, false),
        selected = item == selected,
        onClick = { selected = item },
        title = { Text(context.screen.renderable.render(item)) }
      )
    }
  }
}
