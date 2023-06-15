/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

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
