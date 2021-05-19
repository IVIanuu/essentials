/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.compose.*
import com.ivianuu.injekt.scope.*
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
      val component = rememberElement<PopupMenuComponent>()
      val scope = rememberCoroutineScope()
      items.forEach { item ->
        key(item) {
          PopupMenuItem(
            onSelected = {
              scope.launch { component.navigator.popTop() }
              item.onSelected()
            },
            content = item.content
          )
        }
      }
    }
  }
}

@Provide @InstallElement<UiScope>
class PopupMenuComponent(val navigator: Navigator)

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
