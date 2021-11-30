/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*

object PopupMenu {
  data class Item(
    val onSelected: () -> Unit,
    val content: @Composable () -> Unit
  )
}

@Composable fun PopupMenu(items: List<PopupMenu.Item>) {
  Popup {
    Column {
      val component = LocalKeyUiElements.current<PopupMenuComponent>()
      val scope = rememberCoroutineScope()
      items.forEach { item ->
        key(item) {
          PopupMenuItem(
            onSelected = {
              scope.launch { component.navigator.pop(component.key) }
              item.onSelected()
            },
            content = item.content
          )
        }
      }
    }
  }
}

@Provide @Element<KeyUiScope>
data class PopupMenuComponent(val key: Key<*>, val navigator: Navigator)

@Composable private fun PopupMenuItem(
  onSelected: () -> Unit,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier.widthIn(min = 200.dp)
      .height(48.dp)
      .clickable(onClick = onSelected),
    contentAlignment = Alignment.CenterStart
  ) {
    Box(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      content()
    }
  }
}
