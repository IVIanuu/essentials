/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.LocalKeyUiElements
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element

object PopupMenu {
  data class Item(
    val onSelected: () -> Unit,
    val content: @Composable () -> Unit
  )
}

@Composable fun PopupMenu(items: List<PopupMenu.Item>) {
  Popup {
    val component = LocalKeyUiElements.current.element<PopupMenuComponent>()

    LazyColumn {
      // todo use items(items) { } once fixed
      items.forEach { item ->
        item {
          PopupMenuItem(
            onSelected = action {
              component.navigator.pop(component.key)
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
