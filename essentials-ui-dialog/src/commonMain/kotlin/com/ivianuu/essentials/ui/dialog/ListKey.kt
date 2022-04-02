/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class ListKey<T : Any>(
  val items: List<Item<T>>,
  val title: String? = null
) : PopupKey<T> {
  data class Item<T>(val value: T, val title: String)
}

@Provide fun listKeyUi(
  key: ListKey<Any>,
  navigator: Navigator
) = SimpleKeyUi<ListKey<Any>> {
  DialogScaffold {
    Dialog(
      title = key.title?.let { { Text(it) } },
      content = {
        val scope = rememberCoroutineScope()
        LazyColumn {
          items(key.items) { item ->
            ListItem(
              modifier = Modifier.clickable {
                scope.launch {
                  navigator.pop(key, item.value)
                }
              },
              title = { Text(item.title) },
            )
          }
        }
      }
    )
  }
}
