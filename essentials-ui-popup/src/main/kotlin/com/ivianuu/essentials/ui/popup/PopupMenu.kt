/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.entryPoint
import kotlinx.coroutines.launch

object PopupMenu {
  data class Item(
    val onSelected: () -> Unit,
    val content: @Composable () -> Unit
  )
}

@Composable fun PopupMenu(items: List<PopupMenu.Item>) {
  Popup {
    Column {
      val component = LocalKeyUiComponent.current.entryPoint<PopupMenuComponent>()
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

@EntryPoint<KeyUiComponent> interface PopupMenuComponent {
  val key: Key<*>
  val navigator: Navigator
}

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
