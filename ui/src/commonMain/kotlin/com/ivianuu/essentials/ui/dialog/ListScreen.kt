/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class ListScreen<T : Any>(
  val items: List<T>,
  val title: String? = null,
  @Inject val renderer: UiRenderer<T>,
) : OverlayScreen<T> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: ListScreen<Any>,
    ) = Ui<ListScreen<Any>> {
      AlertDialog(
        onDismissRequest = action { navigator.pop(screen, null) },
        title = screen.title?.let { { Text(it) } },
        text = {
          VerticalList(decorate = false) {
            items(screen.items) { item ->
              ListItem(
                onClick = action { navigator.pop(screen, item) },
                title = { Text(screen.renderer(item)) },
              )
            }
          }
        },
        buttons = {}
      )
    }
  }
}
