/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastForEach
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.EsModalBottomSheet
import essentials.ui.material.Subheader
import essentials.ui.navigation.*
import essentials.ui.prefs.RadioListItem
import injekt.*

class SingleChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: T,
  val title: String? = null,
  val renderable: UiRenderer<T> = inject
) : OverlayScreen<T>

@Provide @Composable fun SingleChoiceListUi(
  navigator: Navigator,
  screen: SingleChoiceListScreen<Any>,
): Ui<SingleChoiceListScreen<Any>> {
  EsModalBottomSheet(
    onDismissRequest = action { navigator.pop(screen, null) }
  ) {
    if (screen.title != null)
      Subheader { Text(screen.title) }

    screen.items.fastForEach { item ->
      RadioListItem(
        value = item == screen.selected,
        onValueChange = scopedAction { value ->
          navigator.pop(screen, item)
        },
        headlineContent = { Text(screen.renderable.render(item)) }
      )
    }
  }
}
