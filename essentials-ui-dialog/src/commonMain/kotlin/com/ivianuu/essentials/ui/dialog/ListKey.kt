/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.*

data class ListKey<T : Any>(
  val items: List<Item<T>>,
  val title: String? = null
) : PopupKey<T> {
  data class Item<T>(val value: T, val title: String)
}

@Provide fun listKeyUi(
  key: ListKey<Any>,
  navigator: Navigator
) = KeyUi<ListKey<Any>> {
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
