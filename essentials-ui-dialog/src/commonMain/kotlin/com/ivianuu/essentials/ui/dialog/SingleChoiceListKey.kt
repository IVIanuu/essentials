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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class SingleChoiceListKey<T : Any>(
  val items: List<Item<T>>,
  val selectedItem: T,
  val title: String
) : DialogKey<T> {
  data class Item<T : Any>(val value: T, val title: String)
}

@Provide fun singleChoiceListUi(
  key: SingleChoiceListKey<Any>,
  navigator: Navigator,
  strings: CommonStrings
): KeyUi<SingleChoiceListKey<Any>> = {
  DialogScaffold {
    val scope = rememberCoroutineScope()
    SingleChoiceListDialog(
      items = remember {
        key.items
          .map { it.value }
      },
      selectedItem = key.selectedItem,
      onSelectionChanged = {
        scope.launch { navigator.pop(key, it) }
      },
      item = { item ->
        Text(key.items.single { it.value == item }.title)
      },
      title = { Text(key.title) },
      buttons = {
        TextButton(onClick = {
          scope.launch { navigator.pop(key, null) }
        }) {
          Text(strings.cancel)
        }
      }
    )
  }
}
