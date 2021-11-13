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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class MultiChoiceListKey<T : Any>(
  val items: List<Item<T>>,
  val selectedItems: Set<T>,
  val title: String
) : DialogKey<Set<T>> {
  data class Item<T>(val value: T, val title: String)
}

@Provide fun multiChoiceListUi(
  key: MultiChoiceListKey<Any>,
  navigator: Navigator,
  strings: CommonStrings
): KeyUi<MultiChoiceListKey<Any>> = {
  DialogScaffold {
    var selectedItems by remember { mutableStateOf(key.selectedItems) }

    MultiChoiceListDialog(
      items = remember {
        key.items
          .map { it.value }
      },
      selectedItems = selectedItems,
      onSelectionsChanged = { selectedItems = it },
      item = { item ->
        Text(key.items.single { it.value == item }.title)
      },
      title = { Text(key.title) },
      buttons = {
        val scope = rememberCoroutineScope()
        TextButton(onClick = {
          scope.launch { navigator.pop(key, null) }
        }) {
          Text(strings.cancel)
        }

        TextButton(
          onClick = {
            scope.launch { navigator.pop(key, selectedItems) }
          }
        ) { Text(strings.ok) }
      }
    )
  }
}
