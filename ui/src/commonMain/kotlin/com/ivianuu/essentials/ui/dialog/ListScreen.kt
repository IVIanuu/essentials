/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class ListScreen<T : Any>(
  val items: List<T>,
  val title: String? = null,
  @Inject val renderer: UiRenderer<T>,
) : DialogScreen<T>

@Provide fun listKeyUi(
  key: ListScreen<Any>,
  navigator: Navigator
) = Ui<ListScreen<Any>, Unit> {
  DialogScaffold {
    Dialog(
      title = key.title?.let { { Text(it) } },
      content = {
        LazyColumn {
          items(key.items) { item ->
            ListItem(
              modifier = Modifier.clickable(onClick = action {
                navigator.pop(key, item)
              }),
              title = { Text(key.renderer(item)) },
            )
          }
        }
      }
    )
  }
}
