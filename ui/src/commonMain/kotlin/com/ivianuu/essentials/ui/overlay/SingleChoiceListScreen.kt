/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.overlay
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.EsListItem
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class SingleChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: T,
  val title: String? = null,
  val renderable: UiRenderer<T> = inject
) : DialogScreen<T> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: SingleChoiceListScreen<Any>,
    ) = Ui<SingleChoiceListScreen<Any>> {
      Dialog(
        title = screen.title?.let { { Text(it) } },
        applyContentPadding = false,
        content = {
          EsLazyColumn(decorators = emptyList()) {
            items(screen.items) { item ->
              EsListItem(
                onClick = scopedAction {
                  navigator.pop(screen, item)
                },
                headlineContent = { Text(screen.renderable.render(item)) },
                trailingContent = {
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
          TextButton(onClick = scopedAction { navigator.pop(screen, null) }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
