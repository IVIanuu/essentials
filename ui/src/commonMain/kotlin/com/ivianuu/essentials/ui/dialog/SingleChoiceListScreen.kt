/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class SingleChoiceListScreen<T : Any>(
  val items: List<T>,
  val selected: T,
  val title: String? = null,
  val renderable: UiRenderer<T> = inject
) : OverlayScreen<T> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: SingleChoiceListScreen<Any>,
    ) = Ui<SingleChoiceListScreen<Any>> {
      AlertDialog(
        onDismissRequest = action { navigator.pop(screen, null) },
        title = screen.title?.let { { Text(it) } },
        text = {
          VerticalList(decorate = false) {
            items(screen.items) { item ->
              ListItem(
                onClick = action { navigator.pop(screen, item) },
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
        confirmButton = {
          TextButton(onClick = action { navigator.pop(screen, null) }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
